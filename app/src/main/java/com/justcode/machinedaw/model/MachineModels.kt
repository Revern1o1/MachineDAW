package com.justcode.machinedaw.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

object MachineTypeIndex {
    const val SINE_TEST = 0
    const val SUBSYNTH = 1
}

@Immutable
data class MachineTypeInfo(
    val typeIndex: Int,
    val typeId: String,
    val displayName: String,
    val category: String,
    val description: String,
)

val AvailableMachineTypes = listOf(
    MachineTypeInfo(
        typeIndex = MachineTypeIndex.SINE_TEST,
        typeId = "sine_test",
        displayName = "Sine Test",
        category = "Synths",
        description = "Minimal test tone (Phase 1)",
    ),
    MachineTypeInfo(
        typeIndex = MachineTypeIndex.SUBSYNTH,
        typeId = "subsynth",
        displayName = "Subsynth",
        category = "Synths",
        description = "Subtractive synth — osc, filter, ADSR",
    ),
)

val MachineColorPalette = listOf(
    Color(0xFF7C4DFF),
    Color(0xFF03DAC6),
    Color(0xFFFF6E40),
    Color(0xFF69F0AE),
    Color(0xFFFFD740),
    Color(0xFF40C4FF),
    Color(0xFFFF4081),
    Color(0xFFB388FF),
    Color(0xFF64FFDA),
    Color(0xFFFFAB40),
    Color(0xFF80D8FF),
    Color(0xFFEA80FC),
    Color(0xFFA7FFEB),
    Color(0xFFFFE57F),
)

enum class MachineLayer { Perform, Shape, Write }

@Immutable
data class MachineTab(
    val engineId: Int,
    val typeIndex: Int,
    val typeId: String,
    val displayName: String,
    val color: Color,
    val isMuted: Boolean = false,
    val activeLayer: MachineLayer = MachineLayer.Perform,
)

@Immutable
data class TransportUiState(
    val isEngineRunning: Boolean = false,
    val isPlaying: Boolean = false,
    val bpm: Float = 120f,
    val sampleRate: Int = 0,
)

@Immutable
data class DawShellUiState(
    val transport: TransportUiState = TransportUiState(),
    val tabs: List<MachineTab> = emptyList(),
    val selectedTabIndex: Int = -1,
    val showMachinePicker: Boolean = false,
    val showTabSwitcher: Boolean = false,
    val meters: FloatArray = FloatArray(14),
) {
    val selectedTab: MachineTab?
        get() = tabs.getOrNull(selectedTabIndex)

    val canAddMachine: Boolean
        get() = tabs.size < 14
}
