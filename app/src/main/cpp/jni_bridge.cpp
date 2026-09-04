#include <jni.h>
#include <android/log.h>

#include "AudioEngine.h"
#include "EngineTypes.h"
#include "MachineRegistry.h"

#include <memory>

#define LOG_TAG "MachineDAW.JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static std::unique_ptr<AudioEngine> gEngine;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeStart(JNIEnv*, jobject) {
    if (!gEngine) gEngine = std::make_unique<AudioEngine>();
    const bool ok = gEngine->start();
    LOGI("nativeStart → %s", ok ? "OK" : "FAIL");
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeStop(JNIEnv*, jobject) {
    if (gEngine) { gEngine->stop(); LOGI("nativeStop"); }
}

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeIsRunning(JNIEnv*, jobject) {
    return (gEngine && gEngine->isRunning()) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeAddMachine(JNIEnv*, jobject, jint typeIndex) {
    if (!gEngine) gEngine = std::make_unique<AudioEngine>();
    return gEngine->addMachine(typeIndex);
}

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeRemoveMachine(JNIEnv*, jobject, jint machineId) {
    if (!gEngine) return JNI_FALSE;
    return gEngine->removeMachine(machineId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeMachineCount(JNIEnv*, jobject) {
    return gEngine ? gEngine->machineCount() : 0;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeTypeCount(JNIEnv*, jobject) {
    return MachineRegistry::instance().typeCount();
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeNoteOn(JNIEnv*, jobject, jint machineId, jint note, jfloat velocity) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::NoteOn, machineId, note, velocity};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (NoteOn)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeNoteOff(JNIEnv*, jobject, jint machineId, jint note) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::NoteOff, machineId, note, 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetParam(JNIEnv*, jobject, jint machineId, jint paramId, jfloat value) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetParam, machineId, paramId, value};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (SetParam)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetMacro(JNIEnv*, jobject, jint machineId, jint macroIndex, jfloat value) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetMacro, machineId, macroIndex, value};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (SetMacro)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetTransportState(JNIEnv*, jobject, jboolean playing) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetTransportState, -1, playing ? 0 : 1, playing ? 1.0f : 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetMute(JNIEnv*, jobject, jint machineId, jboolean muted) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetMute, machineId, 0, muted ? 1.0f : 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetBpm(JNIEnv*, jobject, jfloat bpm) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetBpm, -1, 0, bpm};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetPatternStep(
        JNIEnv*, jobject, jint machineId, jint bank, jint step, jint note, jfloat velocity) {
    if (!gEngine) return;
    const int32_t packed = ((bank & 0xFF) << 16) | ((step & 0xFF) << 8) | (note & 0xFF);
    EngineMessage msg{MessageType::SetPatternStep, machineId, packed, velocity};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (SetPatternStep)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetActivePattern(
        JNIEnv*, jobject, jint machineId, jint bank) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetActivePattern, machineId, bank, 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT jfloatArray JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetMeters(JNIEnv* env, jobject) {
    jfloatArray result = env->NewFloatArray(kMaxMachines);
    if (!result) return nullptr;
    float zeros[kMaxMachines] = {};
    if (gEngine) {
        EngineSnapshot snap = gEngine->getSnapshot();
        env->SetFloatArrayRegion(result, 0, kMaxMachines, snap.meters);
    } else {
        env->SetFloatArrayRegion(result, 0, kMaxMachines, zeros);
    }
    return result;
}

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeIsPlaying(JNIEnv*, jobject) {
    return (gEngine && gEngine->getSnapshot().isPlaying) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetSampleRate(JNIEnv*, jobject) {
    return gEngine ? gEngine->getSnapshot().sampleRate : 0;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetCurrentStep(JNIEnv*, jobject) {
    return gEngine ? gEngine->getSnapshot().currentStep : 0;
}

JNIEXPORT jfloat JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetBpm(JNIEnv*, jobject) {
    return gEngine ? gEngine->getSnapshot().bpm : 120.0f;
}

JNIEXPORT jintArray JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetBbt(JNIEnv* env, jobject) {
    jintArray result = env->NewIntArray(3);
    if (!result) return nullptr;
    jint bbt[3] = {0, 0, 0};
    if (gEngine) {
        EngineSnapshot snap = gEngine->getSnapshot();
        bbt[0] = snap.bar; bbt[1] = snap.beat; bbt[2] = snap.tick;
    }
    env->SetIntArrayRegion(result, 0, 3, bbt);
    return result;
}

} // extern "C"
