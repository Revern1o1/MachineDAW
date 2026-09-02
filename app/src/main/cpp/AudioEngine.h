#pragma once

#include "EngineTypes.h"
#include "Machine.h"
#include "MachineRegistry.h"
#include "SpscRingBuffer.h"

#include <oboe/Oboe.h>
#include <atomic>
#include <memory>

class AudioEngine : public oboe::AudioStreamDataCallback,
                    public oboe::AudioStreamErrorCallback {
public:
    AudioEngine();
    ~AudioEngine() override;

    bool start();
    void stop();
    bool isRunning() const;

    bool enqueueMessage(const EngineMessage& msg);
    EngineSnapshot getSnapshot() const;

    int32_t addMachine(int32_t typeIndex);
    bool removeMachine(int32_t machineId);
    int32_t machineCount() const;

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream* stream,
            void* audioData,
            int32_t numFrames) override;

    void onErrorAfterClose(oboe::AudioStream* stream, oboe::Result error) override;

private:
    void processMessages(int32_t maxMessages);
    void publishSnapshot(int32_t numFrames);

    struct Slot {
        std::unique_ptr<Machine> machine;
        int32_t id = -1;
        std::atomic<bool> active{false};
    };

    Slot slots_[kMaxMachines];
    int32_t nextId_ = 0;

    std::shared_ptr<oboe::AudioStream> stream_;
    SpscRingBuffer<EngineMessage, 256> messageQueue_;

    EngineSnapshot snapshots_[2]{};
    std::atomic<int> publishedIdx_{0};

    float peakMeters_[kMaxMachines] = {};

    std::atomic<bool> isPlaying_{false};
    std::atomic<bool> isRunning_{false};

    int32_t sampleRate_ = 48000;
    int32_t channelCount_ = 2;
    int32_t framesPerBurst_ = 0;

    static constexpr int32_t kMaxFramesPerCallback = 1024;
    float mixScratch_[kMaxFramesPerCallback * 2] = {};
};
