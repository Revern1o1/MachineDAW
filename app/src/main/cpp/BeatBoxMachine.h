#pragma once

#include "Machine.h"
#include "EngineTypes.h"

#include <cstdint>

/**
 * Procedural 8-pad drum machine (Milestone C).
 * Pads: Kick, Snare, HatC, HatO, Tom, Clap, Rim, Perc
 */
class BeatBoxMachine : public Machine {
public:
    static const MachineDefinition kDefinition;
    static constexpr int32_t kPadCount = 8;

    BeatBoxMachine();

    void render(float* out, int32_t numFrames, int32_t channelCount) override;
    void noteOn(int32_t note, float velocity) override;
    void noteOff(int32_t note) override;
    void setParam(int32_t paramId, float value) override;
    float getParam(int32_t paramId) const override;
    void setMacro(int32_t macroIndex, float value) override;
    void setSampleRate(int32_t sampleRate) override;
    const char* name() const override { return "BeatBox"; }
    const MachineDefinition* definition() const override { return &kDefinition; }

private:
    struct Voice {
        bool  active = false;
        int32_t pad = 0;
        float velocity = 1.0f;
        float env = 0.0f;
        float phase = 0.0f;
        float phaseInc = 0.0f;
        uint32_t noiseState = 0;
        float age = 0.0f;
    };

    float renderVoice(Voice& v);
    void triggerPad(int32_t pad, float velocity);

    int32_t sampleRate_ = 48000;
    float level_ = 0.7f;
    float punch_ = 0.5f;
    float grit_  = 0.4f;
    float decay_ = 0.5f;

    static constexpr int32_t kMaxVoices = 16;
    Voice voices_[kMaxVoices]{};
};
