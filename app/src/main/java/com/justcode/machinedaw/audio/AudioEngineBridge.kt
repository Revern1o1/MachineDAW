package com.justcode.machinedaw.audio

/**
 * Single JNI surface into the native audio engine (SDD §5).
 * Milestone A: mute, BPM, pattern steps, playhead/BBT.
 */
object AudioEngineBridge {

    init {
        System.loadLibrary("machinedaw")
    }

    external fun nativeStart(): Boolean
    external fun nativeStop()
    external fun nativeIsRunning(): Boolean

    external fun nativeAddMachine(typeIndex: Int): Int
    external fun nativeRemoveMachine(machineId: Int): Boolean
    external fun nativeMachineCount(): Int
    external fun nativeTypeCount(): Int

    external fun nativeNoteOn(machineId: Int, note: Int, velocity: Float)
    external fun nativeNoteOff(machineId: Int, note: Int)
    external fun nativeSetParam(machineId: Int, paramId: Int, value: Float)
    external fun nativeSetMacro(machineId: Int, macroIndex: Int, value: Float)
    external fun nativeSetTransportState(playing: Boolean)

    external fun nativeSetMute(machineId: Int, muted: Boolean)
    external fun nativeSetBpm(bpm: Float)
    external fun nativeSetPatternStep(
        machineId: Int,
        bank: Int,
        step: Int,
        note: Int,
        velocity: Float,
    )
    external fun nativeSetActivePattern(machineId: Int, bank: Int)
    external fun nativeGetCurrentStep(): Int
    external fun nativeGetBpm(): Float
    external fun nativeGetBbt(): IntArray

    external fun nativeGetMeters(): FloatArray
    external fun nativeIsPlaying(): Boolean
    external fun nativeGetSampleRate(): Int

    const val TYPE_SINE_TEST = 0
    const val TYPE_SWARM = 1
}
