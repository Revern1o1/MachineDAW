package com.justcode.machinedaw.model

import androidx.compose.runtime.Immutable

enum class ParamKindUi { Continuous, Discrete }

@Immutable
data class ParamUiDef(
    val id: Int,
    val name: String,
    val kind: ParamKindUi,
    val min: Float,
    val max: Float,
    val default: Float,
    val unit: String = "",
    val group: String = "Params",
)

object MachineParamCatalog {
    private val sineTest = listOf(
        ParamUiDef(0, "Frequency", ParamKindUi.Continuous, 40f, 2000f, 440f, "Hz", "Oscillator"),
        ParamUiDef(1, "Amplitude", ParamKindUi.Continuous, 0f, 1f, 0.25f, "", "Oscillator"),
    )

    private val subsynth = listOf(
        ParamUiDef(0, "Waveform", ParamKindUi.Discrete, 0f, 1f, 0f, "", "Oscillator"),
        ParamUiDef(1, "Cutoff", ParamKindUi.Continuous, 100f, 8000f, 1200f, "Hz", "Filter"),
        ParamUiDef(2, "Resonance", ParamKindUi.Continuous, 0f, 1f, 0.2f, "", "Filter"),
        ParamUiDef(3, "Attack", ParamKindUi.Continuous, 0f, 1f, 0.01f, "", "Amp Envelope"),
        ParamUiDef(4, "Decay", ParamKindUi.Continuous, 0f, 1f, 0.2f, "", "Amp Envelope"),
        ParamUiDef(5, "Sustain", ParamKindUi.Continuous, 0f, 1f, 0.7f, "", "Amp Envelope"),
        ParamUiDef(6, "Release", ParamKindUi.Continuous, 0f, 1f, 0.3f, "", "Amp Envelope"),
        ParamUiDef(7, "Level", ParamKindUi.Continuous, 0f, 1f, 0.4f, "", "Amp Envelope"),
    )

    fun paramsFor(typeId: String): List<ParamUiDef> = when (typeId) {
        "sine_test" -> sineTest
        "subsynth" -> subsynth
        else -> emptyList()
    }

    fun macrosFor(typeId: String): List<String> = when (typeId) {
        "sine_test" -> listOf("Pitch", "Level", "Macro 2", "Macro 3")
        "subsynth" -> listOf("Character", "Brightness", "Body", "Level")
        else -> listOf("Macro 0", "Macro 1", "Macro 2", "Macro 3")
    }
}
