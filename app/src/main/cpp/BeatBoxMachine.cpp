#include "BeatBoxMachine.h"

#include <algorithm>
#include <cmath>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

static const ParamDef kBeatBoxParams[] = {
    {"Level", ParamKind::Continuous, 0.0f, 1.0f, 0.7f, ""},
    {"Punch", ParamKind::Continuous, 0.0f, 1.0f, 0.5f, ""},
    {"Grit",  ParamKind::Continuous, 0.0f, 1.0f, 0.4f, ""},
    {"Decay", ParamKind::Continuous, 0.0f, 1.0f, 0.5f, ""},
};

const MachineDefinition BeatBoxMachine::kDefinition = {
    "beatbox",
    "BeatBox",
    "Drums",
    kBeatBoxParams,
    4,
    4,
    EditorLayoutHint::BeatBox,
    SequencerKind::DrumSequencer,
    8
};

BeatBoxMachine::BeatBoxMachine() = default;

void BeatBoxMachine::setSampleRate(int32_t sampleRate) {
    if (sampleRate > 0) sampleRate_ = sampleRate;
}

void BeatBoxMachine::setParam(int32_t paramId, float value) {
    value = std::clamp(value, 0.0f, 1.0f);
    switch (paramId) {
        case 0: level_ = value; break;
        case 1: punch_ = value; break;
        case 2: grit_  = value; break;
        case 3: decay_ = value; break;
        default: break;
    }
}

float BeatBoxMachine::getParam(int32_t paramId) const {
    switch (paramId) {
        case 0: return level_;
        case 1: return punch_;
        case 2: return grit_;
        case 3: return decay_;
        default: return 0.0f;
    }
}

void BeatBoxMachine::setMacro(int32_t macroIndex, float value) {
    value = std::clamp(value, 0.0f, 1.0f);
    switch (macroIndex) {
        case 0: setParam(1, value); break;
        case 1: setParam(2, value); break;
        case 2: setParam(3, value); break;
        case 3: setParam(0, value); break;
        default: break;
    }
}

void BeatBoxMachine::noteOn(int32_t note, float velocity) {
    int32_t pad = note;
    if (pad < 0) return;
    if (pad > 7) pad = pad % 8;
    triggerPad(pad, std::clamp(velocity, 0.0f, 1.0f));
}

void BeatBoxMachine::noteOff(int32_t /*note*/) {}

void BeatBoxMachine::triggerPad(int32_t pad, float velocity) {
    int32_t idx = 0;
    float oldest = 1.0e9f;
    for (int32_t i = 0; i < kMaxVoices; ++i) {
        if (!voices_[i].active) { idx = i; break; }
        if (voices_[i].age > oldest) { oldest = voices_[i].age; idx = i; }
    }
    Voice& v = voices_[idx];
    v.active = true;
    v.pad = pad;
    v.velocity = velocity;
    v.env = 1.0f;
    v.phase = 0.0f;
    v.age = 0.0f;
    v.noiseState = 0.5f;
    float hz = 60.0f;
    switch (pad) {
        case 0: hz = 55.0f + punch_ * 40.0f; break;
        case 1: hz = 180.0f; break;
        case 2: hz = 8000.0f; break;
        case 3: hz = 6000.0f; break;
        case 4: hz = 120.0f; break;
        case 5: hz = 900.0f; break;
        case 6: hz = 2000.0f; break;
        case 7: hz = 440.0f; break;
        default: break;
    }
    v.phaseInc = hz / static_cast<float>(sampleRate_);
}

float BeatBoxMachine::renderVoice(Voice& v) {
    if (!v.active) return 0.0f;
    const float sr = static_cast<float>(sampleRate_);
    float baseDecay = 0.08f;
    switch (v.pad) {
        case 0: baseDecay = 0.25f; break;
        case 1: baseDecay = 0.18f; break;
        case 2: baseDecay = 0.04f; break;
        case 3: baseDecay = 0.22f; break;
        case 4: baseDecay = 0.20f; break;
        case 5: baseDecay = 0.12f; break;
        case 6: baseDecay = 0.03f; break;
        case 7: baseDecay = 0.08f; break;
        default: break;
    }
    const float decaySec = baseDecay * (0.4f + decay_ * 1.2f);
    const float envRate = 1.0f / std::max(0.001f, decaySec * sr);
    v.env -= envRate;
    v.age += 1.0f / sr;
    if (v.env <= 0.0f) { v.active = false; v.env = 0.0f; return 0.0f; }
    v.noiseState = v.noiseState * 1103515245.0f + 12345.0f;
    float noise = std::fmod(v.noiseState, 1.0f) * 2.0f - 1.0f;
    float tone = std::sin(2.0f * static_cast<float>(M_PI) * v.phase);
    v.phase += v.phaseInc;
    if (v.phase >= 1.0f) v.phase -= 1.0f;
    if (v.pad == 0) v.phaseInc *= 0.9994f;
    float sample = 0.0f;
    switch (v.pad) {
        case 0: sample = tone * v.env + noise * 0.15f * punch_ * v.env; break;
        case 1: sample = tone * 0.3f * v.env + noise * (0.4f + grit_ * 0.5f) * v.env; break;
        case 2: sample = noise * v.env * (0.5f + grit_ * 0.4f); break;
        case 3: sample = noise * v.env * (0.45f + grit_ * 0.4f); break;
        case 4: sample = tone * v.env; break;
        case 5: sample = noise * v.env * (0.6f + grit_ * 0.3f); break;
        case 6: sample = (tone * 0.4f + noise * 0.6f) * v.env; break;
        case 7: sample = tone * v.env * 0.7f; break;
        default: sample = tone * v.env; break;
    }
    return sample * v.velocity * level_;
}

void BeatBoxMachine::render(float* out, int32_t numFrames, int32_t channelCount) {
    for (int32_t i = 0; i < numFrames * channelCount; ++i) out[i] = 0.0f;
    for (int32_t f = 0; f < numFrames; ++f) {
        float mix = 0.0f;
        for (int32_t vi = 0; vi < kMaxVoices; ++vi) mix += renderVoice(voices_[vi]);
        mix = std::tanh(mix * 1.2f);
        if (channelCount >= 2) {
            out[f * channelCount] += mix;
            out[f * channelCount + 1] += mix;
        } else {
            out[f] += mix;
        }
    }
}
