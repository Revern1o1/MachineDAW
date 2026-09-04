#pragma once

#include <cstdint>

static constexpr int32_t kMaxMachines      = 14;
static constexpr int32_t kMaxParams        = 32;
static constexpr int32_t kMaxMacros        = 4;
static constexpr int32_t kMaxRoutesPerMacro = 4;
static constexpr int32_t kMaxSampleSlots   = 8;
static constexpr int32_t kMaxBulkParams    = 48;
static constexpr int32_t kPatternSteps     = 16;
static constexpr int32_t kPatternBanks     = 8;
static constexpr int32_t kDefaultNote      = 60;

enum class MessageType : uint8_t {
    NoteOn = 0,
    NoteOff,
    SetParam,
    SetMacro,
    SetMacroMapping,
    SetTransportState,
    AddMachine,
    RemoveMachine,
    ReorderMachine,
    AdoptMachine,
    SetSampleSlot,
    SetMute,
    SetBpm,
    SetPatternStep,
    SetActivePattern,
};

struct EngineMessage {
    MessageType type;
    int32_t     machineId;
    int32_t     paramId;
    float       value;
};

enum class ParamKind : uint8_t {
    Continuous = 0,
    Discrete,
    Boolean
};

struct ParamDef {
    const char* name;
    ParamKind   kind;
    float       minValue;
    float       maxValue;
    float       defaultValue;
    const char* unit;
};

enum class EditorLayoutHint : uint8_t {
    Generic = 0,
    SineTest,
    Swarm,
    BeatBox,
    Byrate
};

enum class SequencerKind : uint8_t {
    PianoRoll = 0,
    DrumSequencer
};

struct MacroRoute {
    uint16_t paramId;
    float    rangeMin;
    float    rangeMax;
};

struct MacroMap {
    MacroRoute routes[kMaxMacros][kMaxRoutesPerMacro];
    uint8_t    routeCount[kMaxMacros];
};

struct MachineDefinition {
    const char*      typeId;
    const char*      displayName;
    const char*      category;
    const ParamDef*  paramDefs;
    uint8_t          paramCount;
    uint8_t          macroCount;
    EditorLayoutHint shapeLayout;
    SequencerKind    writeLayout;
    uint8_t          sampleSlotCount;
};

struct PatternStep {
    bool  active = false;
    int32_t note = kDefaultNote;
    float velocity = 1.0f;
};

struct EngineSnapshot {
    float   meters[kMaxMachines];
    int32_t machineCount;
    int32_t currentStep;
    int64_t playheadSamples;
    bool    isPlaying;
    int32_t sampleRate;
    int32_t framesPerBurst;
    float   bpm;
    int32_t bar;
    int32_t beat;
    int32_t tick;
};
