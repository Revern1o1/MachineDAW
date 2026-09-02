#pragma once

#include "EngineTypes.h"
#include "Machine.h"
#include <cstdint>
#include <functional>
#include <memory>

class MachineRegistry {
public:
    using Factory = std::function<std::unique_ptr<Machine>()>;

    static MachineRegistry& instance();

    void registerType(int32_t typeIndex,
                      const MachineDefinition* def,
                      Factory factory);

    int32_t typeCount() const { return typeCount_; }

    const MachineDefinition* definitionFor(int32_t typeIndex) const;
    const char* typeIdFor(int32_t typeIndex) const;
    std::unique_ptr<Machine> create(int32_t typeIndex) const;
    int32_t indexForTypeId(const char* typeId) const;

private:
    MachineRegistry() = default;
    static constexpr int32_t kMaxTypes = 32;
    struct Entry {
        const MachineDefinition* def = nullptr;
        Factory factory;
        bool    used = false;
    };
    Entry   entries_[kMaxTypes];
    int32_t typeCount_ = 0;
};

void registerBuiltinMachines();
