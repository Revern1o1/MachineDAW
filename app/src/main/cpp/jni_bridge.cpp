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
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeStart(
        JNIEnv*, jobject) {
    if (!gEngine) gEngine = std::make_unique<AudioEngine>();
    const bool ok = gEngine->start();
    LOGI("nativeStart -> %s", ok ? "OK" : "FAIL");
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeStop(
        JNIEnv*, jobject) {
    if (gEngine) { gEngine->stop(); LOGI("nativeStop"); }
}

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeIsRunning(
        JNIEnv*, jobject) {
    return (gEngine && gEngine->isRunning()) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeAddMachine(
        JNIEnv*, jobject, jint typeIndex) {
    if (!gEngine) gEngine = std::make_unique<AudioEngine>();
    return gEngine->addMachine(typeIndex);
}

JNIEXPORT jboolean JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeRemoveMachine(
        JNIEnv*, jobject, jint machineId) {
    if (!gEngine) return JNI_FALSE;
    return gEngine->removeMachine(machineId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeMachineCount(
        JNIEnv*, jobject) {
    if (!gEngine) return 0;
    return gEngine->machineCount();
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeTypeCount(
        JNIEnv*, jobject) {
    return MachineRegistry::instance().typeCount();
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeNoteOn(
        JNIEnv*, jobject, jint machineId, jint note, jfloat velocity) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::NoteOn, machineId, note, velocity};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (NoteOn)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeNoteOff(
        JNIEnv*, jobject, jint machineId, jint note) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::NoteOff, machineId, note, 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetParam(
        JNIEnv*, jobject, jint machineId, jint paramId, jfloat value) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetParam, machineId, paramId, value};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (SetParam)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetMacro(
        JNIEnv*, jobject, jint machineId, jint macroIndex, jfloat value) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetMacro, machineId, macroIndex, value};
    if (!gEngine->enqueueMessage(msg)) LOGE("queue full (SetMacro)");
}

JNIEXPORT void JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeSetTransportState(
        JNIEnv*, jobject, jboolean playing) {
    if (!gEngine) return;
    EngineMessage msg{MessageType::SetTransportState, -1, 0, playing ? 1.0f : 0.0f};
    gEngine->enqueueMessage(msg);
}

JNIEXPORT jfloatArray JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetMeters(
        JNIEnv* env, jobject) {
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
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeIsPlaying(
        JNIEnv*, jobject) {
    if (!gEngine) return JNI_FALSE;
    return gEngine->getSnapshot().isPlaying ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_justcode_machinedaw_audio_AudioEngineBridge_nativeGetSampleRate(
        JNIEnv*, jobject) {
    if (!gEngine) return 0;
    return gEngine->getSnapshot().sampleRate;
}

} // extern "C"
