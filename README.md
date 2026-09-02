# Machine DAW — Android

Native Android DAW inspired by Caustic 3 / reFX Nexus machine model.
Architecture follows the SDD (native engine owns canonical audio/project state;
Compose is a reactive view).

## Current status: **Phase 0–2 complete & polished**

Version `0.2.0-phase2`.

### Phase 2 deliverable

> Multi-machine rack driven by `MachineRegistry`.
> Adding a new machine type = implement `Machine` + one registration line.

| Component | Status |
|-----------|--------|
| `MachineDefinition` / static param layout | ✅ |
| `Machine` interface (+ setMacro) | ✅ |
| `MachineRegistry` + factory registration | ✅ |
| `SineTestMachine` (type 0) | ✅ |
| `SubsynthMachine` (type 1) | ✅ |
| Multi-slot rack (up to 14) | ✅ |
| Lock-free double-buffered snapshot | ✅ |
| Atomic slot `active` flags (no data race) | ✅ |
| Edge-to-edge + `WindowInsets.safeDrawing` | ✅ |
| Material 3 theme (full color roles + typography) | ✅ |
| Accessibility semantics on notes / meters / macros | ✅ |
| ≥48 dp touch targets on note pads | ✅ |

### Real-time rules (enforced)

On the audio callback:
- No heap allocation
- No locking (snapshot is double-buffered + atomic index)
- No blocking I/O
- No logging

`Slot.active` is `std::atomic<bool>` with release/acquire ordering so the
audio thread never sees a half-constructed `Machine*`.

### How to get the APK (no Android Studio required)

1. Open **Actions** on this repo → **Build Debug APK** → latest run.
2. Download the **MachineDAW-debug** artifact.
3. Unzip and install `app-debug.apk` on your phone (enable install from unknown sources / use `adb install`).

You can also trigger a build manually: Actions → Build Debug APK → Run workflow.

### Next: Phase 3 — DAW Shell

Transport bar, tab strip, machine header, Perform / Shape / Write host.
State will move into a ViewModel (`collectAsStateWithLifecycle` is ready
via `lifecycle-runtime-compose`).

### Requirements

- minSdk 26, targetSdk 35, compileSdk 35
- ABIs: arm64-v8a, armeabi-v7a, x86_64
- Oboe 1.9.0 via prefab
- Kotlin 2.0 / Compose BOM 2024.10.01
