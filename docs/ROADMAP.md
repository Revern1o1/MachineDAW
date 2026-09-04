# Machine DAW — Roadmap to polished APK

**Sources of truth**
- Architecture: SDD.md · SSD.md · UI_PLAN.md
- Visual: `docs/ui-kit/`
- Code: Phases 0–4 + visual shell V1–V3 + **Milestone A (in progress)**

## Done

| Phase | Shipped |
|-------|---------|
| 0–2 | Project, Oboe, SineTest, Swarm (was Subsynth), rack |
| 3–4 | Shell, Perform/Shape/Write host |
| V1–V3 | Tokens, transport, empty state |
| **A (partial)** | Pattern clock, mute gain, step write, BBT, keyboard, **Swarm rename** |

## Milestone A checklist

- [x] Rename Subsynth → **Swarm**
- [x] Native pattern clock (16 steps/bar, BPM-driven)
- [x] Write steps → engine (`SetPatternStep`)
- [x] Mute → mix gain path (`SetMute`)
- [x] Transport BBT from playhead
- [x] Melodic keyboard press/release
- [ ] CI green APK with all native sources synced

## Next

**B — Presets** · **C — BeatBox** · **D — Studio chrome** · **E — Byrate + polish**

Caps: 14 machines · 2 FX · 4 macros · 8 pads · A–H · 16 steps · minSdk 26
