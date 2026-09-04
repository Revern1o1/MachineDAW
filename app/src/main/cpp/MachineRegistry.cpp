#include "MachineRegistry.h"
#include "SineTestMachine.h"
#include "SwarmMachine.h"

#include <cstring>

MachineRegistry& MachineRegistry::instance() {
    static MachineRegistry reg;
    return reg;
}

void MachineRegistry::registerType(int32_t typeIndex,
                                   const MachineDefinition* def,
                                   Factory factory) {
    if (typeIndex < 0 || typeIndex >= kMaxTypes || !def || !factory) {
        return;
    }
    entries_[typeIndex].def = def;
    entries_[typeIndex].factory = std::move(factory);
    entries_[typeIndex].used = true;
    if (typeIndex + 1 > typeCount_) {
        typeCount_ = typeIndex + 1;
    }
}

const MachineDefinition* MachineRegistry::definitionFor(int32_t typeIndex) const {
    if (typeIndex < 0 || typeIndex >= kMaxTypes || !entries_[typeIndex].used) {
        return nullptr;
    }
    return entries_[typeIndex].def;
}

const char* MachineRegistry::typeIdFor(int32_t typeIndex) const {
    auto* def = definitionFor(typeIndex);
    return def ? def->typeId : nullptr;
}

std::unique_ptr<Machine> MachineRegistry::create(int32_t typeIndex) const {
    if (typeIndex < 0 || typeIndex >= kMaxTypes || !entries_[typeIndex].used) {
        return nullptr;
    }
    return entries_[typeIndex].factory();
}

int32_t MachineRegistry::indexForTypeId(const char* typeId) const {
    if (!typeId) return -1;
    for (int32_t i = 0; i < kMaxTypes; ++i) {
        if (entries_[i].used && entries_[i].def &&
            std::strcmp(entries_[i].def->typeId, typeId) == 0) {
            return i;
        }
    }
    return -1;
}

void registerBuiltinMachines() {
    auto& reg = MachineRegistry::instance();

    reg.registerType(0, &SineTestMachine::kDefinition, []() {
        return std::make_unique<SineTestMachine>();
    });

    // Type 1 — Swarm (formerly Subsynth)
    reg.registerType(1, &SwarmMachine::kDefinition, []() {
        return std::make_unique<SwarmMachine>();
    });
}
