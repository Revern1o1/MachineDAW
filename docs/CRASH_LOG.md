# MachineDAW — Crash / Bug Log

Living document for errors, build failures, runtime crashes, and fixes.
**Any developer working on this project should read and append here.**

Format for new entries:

```
## YYYY-MM-DD — Short title
- **Severity**: crash | build-fail | logic-bug | UX | CI
- **Where**: file / layer / device
- **Symptom**: what happened
- **Root cause**: (if known)
- **Fix / workaround**:
- **Status**: open | fixed | mitigated
```

---

## 2026-09-11 — Missing Gradle Wrapper (gradlew / jar)

- **Severity**: build-fail (local)
- **Where**: repo root / `gradle/wrapper/`
- **Symptom**: Cannot run `./gradlew` or `gradlew.bat`; `gradle-wrapper.jar` absent. CI worked because Actions used system Gradle via `gradle/actions/setup-gradle` with an explicit version.
- **Root cause**: Wrapper scripts and jar were never committed (only `gradle-wrapper.properties` existed).
- **Fix / workaround**: Added `gradlew`, `gradlew.bat`, and `scripts/ensure-wrapper.sh` which downloads the official Gradle 8.9 `gradle-wrapper.jar` from `github.com/gradle/gradle` when missing. CI runs ensure-wrapper then `./gradlew assembleDebug`.
- **Status**: fixed

## 2026-09-11 — BeatBoxMachine noise RNG produced poor / non-deterministic noise

- **Severity**: logic-bug (audio quality)
- **Where**: `app/src/main/cpp/BeatBoxMachine.h`, `BeatBoxMachine.cpp`
- **Symptom**: Noise voice used `float noiseState` with LCG that did not behave as a proper 32-bit integer PRNG; seed was a fixed `0.5f`.
- **Root cause**: Float LCG + `fmod` is a weak pattern for audio noise; type should be `uint32_t`.
- **Fix / workaround**: `noiseState` → `uint32_t`; seed per pad; integer LCG and normalize to [-1, 1].
- **Status**: fixed

## 2026-09-11 — Transport BBT display hard-coded

- **Severity**: UX / logic-bug
- **Where**: `app/src/main/java/.../ui/shell/TransportBar.kt`
- **Symptom**: Transport always showed `"04:02:768"` instead of live bar:beat:tick from engine state.
- **Root cause**: Placeholder string left in UI.
- **Fix / workaround**: `String.format("%02d:%d:%03d", state.bar + 1, state.beat + 1, state.tick)`.
- **Status**: fixed

## 2026-09-11 — Launcher icons referenced drawable instead of mipmap

- **Severity**: UX / resource
- **Where**: `app/src/main/AndroidManifest.xml`
- **Symptom**: `android:icon` / `roundIcon` pointed at `@drawable/ic_launcher_foreground*` while adaptive icons live under `mipmap-anydpi-v26`.
- **Root cause**: Manifest not updated after adaptive icon resources were added.
- **Fix / workaround**: Use `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.
- **Status**: fixed

## Template for future entries

When you hit a crash or a hard-to-reproduce bug:

1. Capture logcat / stacktrace (or CI log URL).
2. Note device/API level or CI runner image if relevant.
3. Append a dated section above the template.
4. Prefer minimal repro steps so the next person can verify the fix.
