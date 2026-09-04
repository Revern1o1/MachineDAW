#pragma once

#include "Machine.h"
#include "EngineTypes.h"

#include <cstdint>

/**
 * Swarm — subtractive synth (formerly Subsynth).
 * Params 0–7: waveform, cutoff, resonance, ADSR, level.
 * Macros: Character, Brightness, Body, Level.
 */
class SwarmMachine : public Machine {
public:
    static const MachineDefinition kDefinition;

    SwarmMachine();

    void render(float* out, int32_t numFrames, int32_t channelCount) override;
    void noteOn(int32_t note, float velocity) override;
    void noteOff(int32_t note) override;
    void setParam(int32_t paramId, float value) override;
    float getParam(int32_t paramId) const override;
    void setMacro(int32_t macroIndex, float value) override;
    void setSampleRate(int32_t sampleRate) override;
    const char* name() const override { return "Swarm"; }
    const MachineDefinition* definition() const override { return &kDefinition; }

private:
    enum class EnvStage : uint8_t { Idle, Attack, Decay, Sustain, Release };

    void updateFilterCoeffs();
    float nextOscSample();
    float processFilter(float in);
    float processEnvelope();

    int32_t sampleRate_ = 48000;

    float waveform_  = 0.0f;
    float cutoff_    = 1200.0f;
    float resonance_ = 0.2f;
    float attack_    = 0.01f;
    float decay_     = 0.2f;
    float sustain_   = 0.7f;
    float release_   = 0.3f;
    float level_     = 0.4f;

    float phase_     = 0.0f;
    float phaseInc_  = 0.0f;
    float frequency_ = 110.0f;

    float filterZ_   = 0.0f;
    float filterCoeff_ = 0.0f;
    float filterFb_  = 0.0f;

    EnvStage envStage_ = EnvStage::Idle;
    float    envLevel_ = 0.0f;
    float    envRate_  = 0.0f;
    float    velocity_ = 1.0f;
    bool     gate_     = false;

    float macroValues_[kMaxMacros] = {0.5f, 0.2f, 0.7f, 0.4f};
};
