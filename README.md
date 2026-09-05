# Machine DAW — Android

Native Android DAW inspired by Caustic 3 / reFX Nexus machine model.
Architecture follows the SDD (native engine owns canonical audio/project state;
Compose is a reactive view).

## Current status: **Milestone B in progress**

Version `0.7.0-milestone-b`.

| Layer | Status |
|-------|--------|
| Oboe + CMake + CI APK | ✅ |
| `AudioEngine` lock-free messages + snapshot | ✅ |
| `MachineRegistry` + `SineTestMachine` + **Swarm** | ✅ |
| DAW shell (transport, tabs, header, layers) | ✅ |
| Visual shell tokens (steel/ink, kit chrome) | ✅ |
| **Pattern clock** (16 steps/bar, native) | ✅ |
| **Write layer → engine steps** | ✅ |
| **Mute via mixer gain path** | ✅ |
| **BBT + current step** in transport / Write playhead | ✅ |
| **Melodic keyboard** press/release → noteOn/noteOff | ✅ |
| **Swarm factory presets** (8) + header ◀ ▶ | ✅ |
| **Preset browser sheet** (Factory list) | ✅ |
| Native bulk PresetStore path | ⏳ sequential SetParam for now |
| Macro mapping popover (kit 15) | ⏳ |

### Real-time rules (enforced)

On the audio callback:
- No heap allocation
- No locking (snapshot is double-buffered + atomic index)
- No blocking I/O
- No logging

`Slot.active` / `Slot.muted` are `std::atomic<bool>` with release/acquire ordering.

### How to get the APK (no Android Studio required)

1. Open **Actions** on this repo → **Build Debug APK** → latest successful run.
2. Download the **MachineDAW-debug** artifact.
3. Unzip and install `app-debug.apk` on your phone (enable install from unknown sources, or `adb install app-debug.apk`).

You can also trigger a build manually: **Actions → Build Debug APK → Run workflow**.

### How to verify (device)

1. Start engine → **+ Swarm** (loads **Init** preset).
2. **Perform**: hold keyboard keys — notes gate on press/release.
3. Header preset: tap **◀ / ▶** to cycle Soft Pad, Pluck, Bass, Bright Lead, …
4. Tap the preset **name** → Factory browser sheet → pick Glass / Warm Keys / etc.
5. **Write**: arm steps → Play — playhead advances; steps fire.
6. **Mute** a tab — mixer gain path silences that machine.

### Swarm factory presets

Init · Soft Pad · Pluck · Bass · Bright Lead · Swarm Drift · Glass · Warm Keys

### Piano roll

Full melodic **piano roll** is **Milestone E** (kit 04). Write today is the interim 16-step melodic grid.

### Next

- Macro mapping popover (kit 15)
- Native bulk param load (SDD §4.1)
- Milestone C — BeatBox

### Requirements

- minSdk 26, targetSdk 35, compileSdk 35
- ABIs: arm64-v8a, armeabi-v7a, x86_64
- Oboe via prefab
- Kotlin 2.0 / Compose BOM 2024.10.01

See `docs/ROADMAP.md` for the full plan. Visual source of truth: `docs/ui-kit/`.
