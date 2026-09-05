package com.justcode.machinedaw.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Matches native MachineRegistry type indices. */
object MachineTypeIndex {
    const val SINE_TEST = 0
    const val SWARM = 1
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
        description = "Minimal test tone",
    ),
    MachineTypeInfo(
        typeIndex = MachineTypeIndex.SWARM,
        typeId = "swarm",
        displayName = "Swarm",
        category = "Synths",
        description = "Subtractive synth — osc, filter, ADSR",
    ),
)

/** Kit machine palette — see ui.theme.MachineColors.MachinePalette */
val MachineColorPalette = com.justcode.machinedaw.ui.theme.MachineColors.MachinePalette

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
    /** Current preset id in PresetLibrary (factory or later user). */
    val presetId: String? = null,
    val presetName: String = "Default",
    /** Shape param → Perform macro routes (kit 15). */
    val macroRoutes: List<ParamMacroRoute> = emptyList(),
)

@Immutable
data class TransportUiState(
    val isEngineRunning: Boolean = false,
    val isPlaying: Boolean = false,
    val bpm: Float = 120f,
    val sampleRate: Int = 0,
    val bar: Int = 0,
    val beat: Int = 0,
    val tick: Int = 0,
    val currentStep: Int = 0,
)

@Immutable
data class DawShellUiState(
    val transport: TransportUiState = TransportUiState(),
    val tabs: List<MachineTab> = emptyList(),
    val selectedTabIndex: Int = -1,
    val showMachinePicker: Boolean = false,
    val showTabSwitcher: Boolean = false,
    val showPresetBrowser: Boolean = false,
    val meters: FloatArray = FloatArray(14),
) {
    val selectedTab: MachineTab?
        get() = tabs.getOrNull(selectedTabIndex)

    val canAddMachine: Boolean
        get() = tabs.size < 14
}
