# Machine DAW — Roadmap to polished APK

**Sources of truth**
- Architecture: `docs` / SDD.md · SSD.md · UI_PLAN.md (attachments)
- Visual: `docs/ui-kit/` (mockup kit, 2400×1080 landscape)
- Code: Phases 0–4 + Milestone A on tree

Visual target is the UI kit, not Material defaults. Brand chrome = steel/ink;
play green / record red are semantic; machine hues are identity only.

---

## Done (foundation)

| Phase | What shipped |
|-------|----------------|
| **0** | Android project, Oboe, CMake, CI APK |
| **1** | AudioEngine + SineTestMachine, lock-free messages |
| **2** | MachineRegistry, SwarmMachine (was Subsynth), rack, macros |
| **3** | DAW shell: transport, tabs, header, picker, ViewModel |
| **4** | Perform / Shape / Write host, param catalog, Write stub |

### Visual shell pass

| Step | Deliverable | Kit refs |
|------|-------------|----------|
| **V1** | Design tokens + always-dark theme | 00 |
| **V2** | Transport bar (MACHINE wordmark, circular transport, meter pips) | 19, 02 |
| **V3** | Empty workspace (“Add a machine to start”) | 01 |
| **V4** | Tab strip (color chip, mute dot, sticky Mix, count) | 19, 22 |
| **V5** | Machine header (preset ◀▶, layer chips, FX rail stub) | 19, 02 |
| **V6** | Circular macro knobs + docked keyboard shell | 02, 21 |

### Milestone A — Playable shell ✅

- Pattern clock native (SSD §7): 16 steps/bar, samplesPerStep from BPM
- Write: 16-step grid → `SetPatternStep` / `SetActivePattern`
- Transport BBT + `currentStep` from engine snapshot
- Melodic keyboard (note on/off via press/release)
- Mute via mixer gain path (not UI-only)
- Subsynth renamed → **Swarm** (native + Kotlin + param catalog)

Version `0.6.0-milestone-a`. Installable debug APK via GitHub Actions.

---

## Product roadmap (next)

### Milestone B — Preset-centric machine
- PresetStore + bulk load path (SDD §4.1 / SSD §9)
- Header preset browser sheet (kit 09)
- Swarm factory presets
- Macro mapping popover (kit 15)

### Milestone C — BeatBox
- BeatBox machine + SampleStore
- Perform 8-pad grid (kit 05)
- Shape per-pad editor (kit 06)
- Write drum sequencer 8×16 (kit 07)

### Milestone D — Studio chrome
- FX drawer 2 slots + Delay (kit 10)
- Mixer tab sticky (kit 11)
- Song arrangement page-turn (kit 12)
- Tab switcher grid (kit 13)
- Project save/load (SSD §13–14)

### Milestone E — Byrate + polish
- Byrate DSP + Perform filter visual (kit 02–03)
- Piano roll Write (kit 04)
- Accessibility pass (contrast, targets ≥48dp, semantics)
- Release-signed APK, baseline profiles

---

## Caps (unchanged)
14 machines · 2 FX · 4 macros · 8 pads · patterns A–H · 16 steps/bar · minSdk 26

## CI
Every push to `main` builds debug APK. Download from Actions → **MachineDAW-debug**.
