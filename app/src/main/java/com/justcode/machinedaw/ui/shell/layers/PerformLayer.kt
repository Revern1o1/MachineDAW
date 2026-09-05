package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.ui.components.MacroKnob
import com.justcode.machinedaw.ui.components.MelodicKeyboard
import com.justcode.machinedaw.ui.theme.MachineColors

/**
 * Perform — circular macros + docked keyboard (kit V6).
 */
@Composable
fun PerformLayer(
    tab: MachineTab,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    onMacro: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val macroLabels = remember(tab.typeId) { MachineParamCatalog.macrosFor(tab.typeId) }
    val macroValues = remember(tab.engineId, tab.presetId) {
        List(4) { i ->
            mutableFloatStateOf(
                when (i) {
                    0 -> 0.50f
                    1 -> 0.25f
                    2 -> 0.70f
                    else -> 0.40f
                },
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text("Perform", style = MaterialTheme.typography.titleMedium)
        Text(
            "Macros · hold keys to play",
            style = MaterialTheme.typography.bodySmall,
            color = MachineColors.Ink2,
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            macroLabels.take(4).forEachIndexed { index, label ->
                var value by macroValues[index]
                MacroKnob(
                    label = label,
                    value = value,
                    accent = tab.color,
                    onValueChange = {
                        value = it
                        onMacro(index, it)
                    },
                )
            }
        }

        MelodicKeyboard(
            accent = tab.color,
            onNoteOn = onNoteOn,
            onNoteOff = onNoteOff,
        )
    }
}
