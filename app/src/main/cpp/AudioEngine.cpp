#include "AudioEngine.h"

#include <android/log.h>
#include <cmath>
#include <cstring>

#define LOG_TAG "MachineDAW.Engine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

AudioEngine::AudioEngine() {
    registerBuiltinMachines();

    EngineSnapshot zero{};
    zero.isPlaying = false;
    zero.sampleRate = sampleRate_;
    zero.machineCount = 0;
    snapshots_[0] = zero;
    snapshots_[1] = zero;
    publishedIdx_.store(0, std::memory_order_relaxed);
}

AudioEngine::~AudioEngine() {
    stop();
}

bool AudioEngine::start() {
    if (isRunning_.load(std::memory_order_acquire)) {
        return true;
    }

    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
           ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
           ->setSharingMode(oboe::SharingMode::Exclusive)
           ->setFormat(oboe::AudioFormat::Float)
           ->setChannelCount(oboe::ChannelCount::Stereo)
           ->setDataCallback(this)
           ->setErrorCallback(this)
           ->setSampleRate(48000);

    oboe::Result result = builder.openStream(stream_);
    if (result != oboe::Result::OK) {
        LOGE("Exclusive open failed: %s - trying Shared", oboe::convertToText(result));
        builder.setSharingMode(oboe::SharingMode::Shared);
        result = builder.openStream(stream_);
        if (result != oboe::Result::OK) {
            LOGE("Shared open failed: %s", oboe::convertToText(result));
            return false;
        }
    }

    sampleRate_     = stream_->getSampleRate();
    channelCount_   = stream_->getChannelCount();
    framesPerBurst_ = stream_->getFramesPerBurst();

    for (auto& slot : slots_) {
        if (slot.machine) {
            slot.machine->setSampleRate(sampleRate_);
        }
    }

    LOGI("Stream opened: %d Hz, %d ch, burst %d",
         sampleRate_, channelCount_, framesPerBurst_);

    result = stream_->requestStart();
    if (result != oboe::Result::OK) {
        LOGE("requestStart failed: %s", oboe::convertToText(result));
        stream_.reset();
        return false;
    }

    isRunning_.store(true, std::memory_order_release);
    isPlaying_.store(true, std::memory_order_release);
    return true;
}

void AudioEngine::stop() {
    if (!isRunning_.load(std::memory_order_acquire)) {
        return;
    }
    isRunning_.store(false, std::memory_order_release);
    isPlaying_.store(false, std::memory_order_release);

    if (stream_) {
        stream_->requestStop();
        stream_->close();
        stream_.reset();
    }
    LOGI("Stream stopped");
}

bool AudioEngine::isRunning() const {
    return isRunning_.load(std::memory_order_acquire);
}

int32_t AudioEngine::addMachine(int32_t typeIndex) {
    int32_t slotIndex = -1;
    for (int32_t i = 0; i < kMaxMachines; ++i) {
        if (!slots_[i].machine) {
            slotIndex = i;
            break;
        }
    }
    if (slotIndex < 0) {
        LOGE("Machine rack full (%d)", kMaxMachines);
        return -1;
    }

    auto machine = MachineRegistry::instance().create(typeIndex);
    if (!machine) {
        LOGE("Unknown machine type index %d", typeIndex);
        return -1;
    }

    machine->setSampleRate(sampleRate_);

    const int32_t id = nextId_++;
    slots_[slotIndex].machine = std::move(machine);
    slots_[slotIndex].id = id;
    slots_[slotIndex].active.store(true, std::memory_order_release);

    LOGI("Added machine id=%d type=%d slot=%d (%s)",
         id, typeIndex, slotIndex,
         slots_[slotIndex].machine->name());
    return id;
}

bool AudioEngine::removeMachine(int32_t machineId) {
    for (auto& slot : slots_) {
        if (slot.id == machineId && slot.machine) {
            slot.active.store(false, std::memory_order_release);
            slot.machine.reset();
            slot.id = -1;
            LOGI("Removed machine id=%d", machineId);
            return true;
        }
    }
    return false;
}

int32_t AudioEngine::machineCount() const {
    int32_t n = 0;
    for (const auto& slot : slots_) {
        if (slot.active.load(std::memory_order_acquire) && slot.machine) ++n;
    }
    return n;
}

bool AudioEngine::enqueueMessage(const EngineMessage& msg) {
    return messageQueue_.tryPush(msg);
}

EngineSnapshot AudioEngine::getSnapshot() const {
    const int idx = publishedIdx_.load(std::memory_order_acquire);
    return snapshots_[idx];
}

void AudioEngine::processMessages(int32_t maxMessages) {
    EngineMessage msg{};
    int32_t count = 0;
    while (count < maxMessages && messageQueue_.tryPop(msg)) {
        Machine* target = nullptr;
        if (msg.machineId >= 0) {
            for (auto& slot : slots_) {
                if (slot.active.load(std::memory_order_acquire) &&
                    slot.id == msg.machineId && slot.machine) {
                    target = slot.machine.get();
                    break;
                }
            }
        }

        switch (msg.type) {
            case MessageType::NoteOn:
                if (target) target->noteOn(msg.paramId, msg.value);
                break;
            case MessageType::NoteOff:
                if (target) target->noteOff(msg.paramId);
                break;
            case MessageType::SetParam:
                if (target) target->setParam(msg.paramId, msg.value);
                break;
            case MessageType::SetMacro:
                if (target) target->setMacro(msg.paramId, msg.value);
                break;
            case MessageType::SetTransportState:
                isPlaying_.store(msg.value > 0.5f, std::memory_order_relaxed);
                break;
            case MessageType::RemoveMachine:
                for (auto& slot : slots_) {
                    if (slot.id == msg.machineId) {
                        slot.active.store(false, std::memory_order_release);
                        break;
                    }
                }
                break;
            default:
                break;
        }
        ++count;
    }
}

void AudioEngine::publishSnapshot(int32_t /*numFrames*/) {
    const int pub = publishedIdx_.load(std::memory_order_relaxed);
    const int write = 1 - pub;

    EngineSnapshot& snap = snapshots_[write];
    snap.isPlaying       = isPlaying_.load(std::memory_order_relaxed);
    snap.sampleRate      = sampleRate_;
    snap.framesPerBurst  = framesPerBurst_;
    snap.currentStep     = 0;
    snap.playheadSamples = 0;

    int32_t n = 0;
    for (int32_t i = 0; i < kMaxMachines; ++i) {
        if (slots_[i].active.load(std::memory_order_acquire) && slots_[i].machine) {
            snap.meters[n] = peakMeters_[i];
            peakMeters_[i] *= 0.92f;
            ++n;
        }
    }
    for (int32_t i = n; i < kMaxMachines; ++i) {
        snap.meters[i] = 0.0f;
    }
    snap.machineCount = n;

    publishedIdx_.store(write, std::memory_order_release);
}

oboe::DataCallbackResult AudioEngine::onAudioReady(
        oboe::AudioStream* /*stream*/,
        void* audioData,
        int32_t numFrames) {

    auto* out = static_cast<float*>(audioData);
    const int32_t ch = channelCount_;
    const int32_t totalSamples = numFrames * ch;

    if (numFrames > kMaxFramesPerCallback) {
        std::memset(out, 0, static_cast<size_t>(totalSamples) * sizeof(float));
        return oboe::DataCallbackResult::Continue;
    }

    processMessages(64);
    std::memset(out, 0, static_cast<size_t>(totalSamples) * sizeof(float));

    if (isPlaying_.load(std::memory_order_relaxed)) {
        for (int32_t i = 0; i < kMaxMachines; ++i) {
            if (!slots_[i].active.load(std::memory_order_acquire) || !slots_[i].machine) {
                continue;
            }

            std::memset(mixScratch_, 0,
                        static_cast<size_t>(totalSamples) * sizeof(float));
            slots_[i].machine->render(mixScratch_, numFrames, ch);

            float peak = 0.0f;
            for (int32_t s = 0; s < totalSamples; ++s) {
                out[s] += mixScratch_[s];
                peak = std::max(peak, std::fabs(mixScratch_[s]));
            }
            if (peak > peakMeters_[i]) {
                peakMeters_[i] = peak;
            }
        }

        for (int32_t s = 0; s < totalSamples; ++s) {
            float v = out[s];
            if (v > 1.0f) v = 1.0f;
            if (v < -1.0f) v = -1.0f;
            out[s] = v;
        }
    }

    publishSnapshot(numFrames);
    return oboe::DataCallbackResult::Continue;
}

void AudioEngine::onErrorAfterClose(oboe::AudioStream* /*stream*/, oboe::Result error) {
    LOGE("Stream error after close: %s", oboe::convertToText(error));
    isRunning_.store(false, std::memory_order_release);
}
