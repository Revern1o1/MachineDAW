package com.justcode.machinedaw.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab

@Composable
fun MachineContent(
    tab: MachineTab,
    onLayerChange: (MachineLayer) -> Unit,
    onNoteOn: (Int) -> Unit,
    onNoteOffAll: () -> Unit,
    onMacro: (Int, Float) -> Unit,
    onParam: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    MachineLayerHost(
        tab = tab,
        onLayerChange = onLayerChange,
        onNoteOn = onNoteOn,
        onNoteOffAll = onNoteOffAll,
        onMacro = onMacro,
        onParam = onParam,
        modifier = modifier,
    )
}
