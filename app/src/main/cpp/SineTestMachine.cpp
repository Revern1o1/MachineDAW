#include "SineTestMachine.h"
#include <algorithm>
#include <cmath>
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

static const ParamDef kSineParams[] = {
    {"Frequency", ParamKind::Continuous, 40.0f, 2000.0f, 440.0f, "Hz"},
    {"Amplitude", ParamKind::Continuous, 0.0f, 1.0f, 0.25f, ""},
};

const MachineDefinition SineTestMachine::kDefinition = {
    "sine_test", "Sine Test", "Synths", kSineParams, 2, 2,
    EditorLayoutHint::SineTest, SequencerKind::PianoRoll, 0
};

SineTestMachine::SineTestMachine() { updatePhaseIncrement(); }

void SineTestMachine::updatePhaseIncrement() {
    phaseInc_ = (2.0f * static_cast<float>(M_PI) * frequency_) / static_cast<float>(sampleRate_);
}

void SineTestMachine::setSampleRate(int32_t sampleRate) {
    if (sampleRate > 0) { sampleRate_ = sampleRate; updatePhaseIncrement(); }
}

void SineTestMachine::setParam(int32_t paramId, float value) {
    switch (paramId) {
        case 0: frequency_ = std::clamp(value, 40.0f, 2000.0f); updatePhaseIncrement(); break;
        case 1: amplitude_ = std::clamp(value, 0.0f, 1.0f); break;
        default: break;
    }
}

float SineTestMachine::getParam(int32_t paramId) const {
    switch (paramId) {
        case 0: return frequency_;
        case 1: return amplitude_;
        default: return 0.0f;
    }
}

void SineTestMachine::setMacro(int32_t macroIndex, float value) {
    if (macroIndex < 0 || macroIndex >= kMaxMacros) return;
    value = std::clamp(value, 0.0f, 1.0f);
    macroValues_[macroIndex] = value;
    if (macroIndex == 0) setParam(0, 40.0f + value * (2000.0f - 40.0f));
    else if (macroIndex == 1) setParam(1, value);
}

void SineTestMachine::noteOn(int32_t note, float velocity) {
    gate_ = true;
    velocity_ = std::clamp(velocity, 0.0f, 1.0f);
    if (note >= 0 && note <= 127) {
        frequency_ = 440.0f * std::pow(2.0f, (note - 69) / 12.0f);
        frequency_ = std::clamp(frequency_, 40.0f, 2000.0f);
        updatePhaseIncrement();
    }
}

void SineTestMachine::noteOff(int32_t) { gate_ = false; }

void SineTestMachine::render(float* out, int32_t numFrames, int32_t channelCount) {
    const float amp = gate_ ? (amplitude_ * velocity_) : 0.0f;
    for (int32_t i = 0; i < numFrames; ++i) {
        const float sample = std::sin(phase_) * amp;
        phase_ += phaseInc_;
        if (phase_ >= 2.0f * static_cast<float>(M_PI)) phase_ -= 2.0f * static_cast<float>(M_PI);
        for (int32_t c = 0; c < channelCount; ++c) out[i * channelCount + c] += sample;
    }
}
