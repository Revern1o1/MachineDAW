#pragma once

#include "Machine.h"
#include "EngineTypes.h"
#include <cstdint>

class SineTestMachine : public Machine {
public:
    static const MachineDefinition kDefinition;
    SineTestMachine();
    void render(float* out, int32_t numFrames, int32_t channelCount) override;
    void noteOn(int32_t note, float velocity) override;
    void noteOff(int32_t note) override;
    void setParam(int32_t paramId, float value) override;
    float getParam(int32_t paramId) const override;
    void setMacro(int32_t macroIndex, float value) override;
    void setSampleRate(int32_t sampleRate) override;
    const char* name() const override { return "SineTest"; }
    const MachineDefinition* definition() const override { return &kDefinition; }
private:
    void updatePhaseIncrement();
    int32_t sampleRate_ = 48000;
    float frequency_ = 440.0f;
    float amplitude_ = 0.25f;
    float phase_ = 0.0f;
    float phaseInc_ = 0.0f;
    bool gate_ = false;
    float velocity_ = 1.0f;
    float macroValues_[kMaxMacros] = {0.5f, 0.25f, 0.0f, 0.0f};
};
