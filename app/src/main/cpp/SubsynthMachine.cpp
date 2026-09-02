#include "SubsynthMachine.h"

#include <algorithm>
#include <cmath>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

static const ParamDef kSubsynthParams[] = {
    {"Waveform",  ParamKind::Discrete,   0.0f, 1.0f, 0.0f, ""},
    {"Cutoff",    ParamKind::Continuous, 100.0f, 8000.0f, 1200.0f, "Hz"},
    {"Resonance", ParamKind::Continuous, 0.0f, 1.0f, 0.2f, ""},
    {"Attack",    ParamKind::Continuous, 0.0f, 1.0f, 0.01f, ""},
    {"Decay",     ParamKind::Continuous, 0.0f, 1.0f, 0.2f, ""},
    {"Sustain",   ParamKind::Continuous, 0.0f, 1.0f, 0.7f, ""},
    {"Release",   ParamKind::Continuous, 0.0f, 1.0f, 0.3f, ""},
    {"Level",     ParamKind::Continuous, 0.0f, 1.0f, 0.4f, ""},
};

const MachineDefinition SubsynthMachine::kDefinition = {
    "subsynth",
    "Subsynth",
    "Synths",
    kSubsynthParams,
    8,
    4,
    EditorLayoutHint::Subsynth,
    SequencerKind::PianoRoll,
    0
};

SubsynthMachine::SubsynthMachine() {
    updateFilterCoeffs();
}

void SubsynthMachine::setSampleRate(int32_t sampleRate) {
    if (sampleRate > 0) {
        sampleRate_ = sampleRate;
        phaseInc_ = frequency_ / static_cast<float>(sampleRate_);
        updateFilterCoeffs();
    }
}

void SubsynthMachine::updateFilterCoeffs() {
    const float fc = std::clamp(cutoff_, 20.0f, static_cast<float>(sampleRate_) * 0.45f);
    filterCoeff_ = 1.0f - std::exp(-2.0f * static_cast<float>(M_PI) * fc /
                                   static_cast<float>(sampleRate_));
    filterFb_ = std::clamp(resonance_, 0.0f, 0.95f) * 0.95f;
}

void SubsynthMachine::setParam(int32_t paramId, float value) {
    switch (paramId) {
        case 0: waveform_  = value >= 0.5f ? 1.0f : 0.0f; break;
        case 1:
            cutoff_ = std::clamp(value, 100.0f, 8000.0f);
            updateFilterCoeffs();
            break;
        case 2:
            resonance_ = std::clamp(value, 0.0f, 1.0f);
            updateFilterCoeffs();
            break;
        case 3: attack_  = std::clamp(value, 0.0f, 1.0f); break;
        case 4: decay_   = std::clamp(value, 0.0f, 1.0f); break;
        case 5: sustain_ = std::clamp(value, 0.0f, 1.0f); break;
        case 6: release_ = std::clamp(value, 0.0f, 1.0f); break;
        case 7: level_   = std::clamp(value, 0.0f, 1.0f); break;
        default: break;
    }
}

float SubsynthMachine::getParam(int32_t paramId) const {
    switch (paramId) {
        case 0: return waveform_;
        case 1: return cutoff_;
        case 2: return resonance_;
        case 3: return attack_;
        case 4: return decay_;
        case 5: return sustain_;
        case 6: return release_;
        case 7: return level_;
        default: return 0.0f;
    }
}

void SubsynthMachine::setMacro(int32_t macroIndex, float value) {
    if (macroIndex < 0 || macroIndex >= kMaxMacros) return;
    value = std::clamp(value, 0.0f, 1.0f);
    macroValues_[macroIndex] = value;

    switch (macroIndex) {
        case 0:
            setParam(1, 100.0f + value * 7900.0f);
            break;
        case 1:
            setParam(2, value);
            setParam(1, cutoff_ + value * 200.0f);
            break;
        case 2:
            setParam(5, value);
            break;
        case 3:
            setParam(7, value);
            break;
        default:
            break;
    }
}

void SubsynthMachine::noteOn(int32_t note, float velocity) {
    velocity_ = std::clamp(velocity, 0.0f, 1.0f);
    if (note >= 0 && note <= 127) {
        frequency_ = 440.0f * std::pow(2.0f, (note - 69) / 12.0f);
        phaseInc_ = frequency_ / static_cast<float>(sampleRate_);
    }
    gate_ = true;
    envStage_ = EnvStage::Attack;
    const float attackSec = 0.001f + attack_ * 2.0f;
    envRate_ = 1.0f / (attackSec * static_cast<float>(sampleRate_));
}

void SubsynthMachine::noteOff(int32_t /*note*/) {
    gate_ = false;
    envStage_ = EnvStage::Release;
    const float releaseSec = 0.01f + release_ * 3.0f;
    envRate_ = envLevel_ / (releaseSec * static_cast<float>(sampleRate_));
}

float SubsynthMachine::nextOscSample() {
    float s;
    if (waveform_ < 0.5f) {
        s = 2.0f * phase_ - 1.0f;
    } else {
        s = phase_ < 0.5f ? 1.0f : -1.0f;
    }
    phase_ += phaseInc_;
    if (phase_ >= 1.0f) phase_ -= 1.0f;
    return s;
}

float SubsynthMachine::processFilter(float in) {
    const float fb = filterZ_ * filterFb_;
    filterZ_ += filterCoeff_ * (in - filterZ_ + fb);
    if (filterZ_ > 1.0f) filterZ_ = 1.0f;
    if (filterZ_ < -1.0f) filterZ_ = -1.0f;
    return filterZ_;
}

float SubsynthMachine::processEnvelope() {
    switch (envStage_) {
        case EnvStage::Attack:
            envLevel_ += envRate_;
            if (envLevel_ >= 1.0f) {
                envLevel_ = 1.0f;
                envStage_ = EnvStage::Decay;
                const float decaySec = 0.01f + decay_ * 2.0f;
                const float target = sustain_;
                envRate_ = (1.0f - target) / (decaySec * static_cast<float>(sampleRate_));
            }
            break;
        case EnvStage::Decay:
            envLevel_ -= envRate_;
            if (envLevel_ <= sustain_) {
                envLevel_ = sustain_;
                envStage_ = EnvStage::Sustain;
            }
            break;
        case EnvStage::Sustain:
            envLevel_ = sustain_;
            if (!gate_) {
                envStage_ = EnvStage::Release;
                const float releaseSec = 0.01f + release_ * 3.0f;
                envRate_ = envLevel_ / (releaseSec * static_cast<float>(sampleRate_));
            }
            break;
        case EnvStage::Release:
            envLevel_ -= envRate_;
            if (envLevel_ <= 0.0f) {
                envLevel_ = 0.0f;
                envStage_ = EnvStage::Idle;
            }
            break;
        case EnvStage::Idle:
        default:
            envLevel_ = 0.0f;
            break;
    }
    return envLevel_;
}

void SubsynthMachine::render(float* out, int32_t numFrames, int32_t channelCount) {
    for (int32_t i = 0; i < numFrames; ++i) {
        const float env = processEnvelope();
        if (env <= 0.0f && envStage_ == EnvStage::Idle) {
            continue;
        }
        float s = nextOscSample();
        s = processFilter(s);
        s *= env * velocity_ * level_;

        for (int32_t c = 0; c < channelCount; ++c) {
            out[i * channelCount + c] += s;
        }
    }
}
