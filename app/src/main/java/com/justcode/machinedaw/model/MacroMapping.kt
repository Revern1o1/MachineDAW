package com.justcode.machinedaw.model

import androidx.compose.runtime.Immutable

/**
 * One Shape param routed to a Perform macro (kit 15).
 * Macro 0..1 scales the param across [rangeMin, rangeMax].
 */
@Immutable
data class ParamMacroRoute(
    val paramId: Int,
    val macroIndex: Int,
    val rangeMin: Float,
    val rangeMax: Float,
)

object DefaultMacroMaps {
    /** Matches SwarmMachine::setMacro hardwired routes. */
    fun forType(typeId: String): List<ParamMacroRoute> = when (typeId) {
        "swarm" -> listOf(
            ParamMacroRoute(1, 0, 100f, 8000f),   // Character → Cutoff
            ParamMacroRoute(2, 1, 0f, 1f),        // Brightness → Resonance
            ParamMacroRoute(5, 2, 0f, 1f),        // Body → Sustain
            ParamMacroRoute(7, 3, 0f, 1f),        // Level → Level
        )
        "sine_test" -> listOf(
            ParamMacroRoute(0, 0, 40f, 2000f),    // Pitch → Frequency
            ParamMacroRoute(1, 1, 0f, 1f),        // Level → Amplitude
        )
        else -> emptyList()
    }
}
