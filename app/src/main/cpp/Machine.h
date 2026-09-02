#pragma once

#include "EngineTypes.h"
#include <cstdint>

/**
 * Core Machine interface (SDD §4.3).
 * All real-time methods must be allocation-free and lock-free.
 */
class Machine {
public:
    virtual ~Machine() = default;

    virtual void render(float* out, int32_t numFrames, int32_t channelCount) = 0;
    virtual void noteOn(int32_t note, float velocity) = 0;
    virtual void noteOff(int32_t note) = 0;
    virtual void setParam(int32_t paramId, float value) = 0;
    virtual float getParam(int32_t paramId) const = 0;
    virtual void setMacro(int32_t macroIndex, float value) = 0;
    virtual void setSampleSlot(int32_t /*slotIndex*/, const void* /*slotData*/) {
        // Default no-op for pure synths
    }
    virtual void setSampleRate(int32_t sampleRate) = 0;
    virtual const char* name() const = 0;
    virtual const MachineDefinition* definition() const { return nullptr; }
};
