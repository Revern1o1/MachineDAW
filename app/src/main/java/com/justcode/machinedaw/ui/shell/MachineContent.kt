package com.justcode.machinedaw.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab

@Composable
fun MachineContent(
    tab: MachineTab,
    currentStep: Int,
    isPlaying: Boolean,
    onLayerChange: (MachineLayer) -> Unit,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    onMacro: (Int, Float) -> Unit,
    onParam: (Int, Float) -> Unit,
    onMapParamToMacro: (paramId: Int, macroIndex: Int) -> Unit,
    onClearParamMacro: (paramId: Int) -> Unit,
    onSetStep: (bank: Int, step: Int, active: Boolean) -> Unit,
    onSelectBank: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    MachineLayerHost(
        tab = tab,
        currentStep = currentStep,
        isPlaying = isPlaying,
        onLayerChange = onLayerChange,
        onNoteOn = onNoteOn,
        onNoteOff = onNoteOff,
        onMacro = onMacro,
        onParam = onParam,
        onMapParamToMacro = onMapParamToMacro,
        onClearParamMacro = onClearParamMacro,
        onSetStep = onSetStep,
        onSelectBank = onSelectBank,
        modifier = modifier,
    )
}
