package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachineTab

@Composable
fun PerformLayer(
    tab: MachineTab,
    onNoteOn: (Int) -> Unit,
    onNoteOffAll: () -> Unit,
    onMacro: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val macroLabels = remember(tab.typeId) { MachineParamCatalog.macrosFor(tab.typeId) }
    val macroValues = remember(tab.engineId) {
        listOf(
            mutableFloatStateOf(0.5f),
            mutableFloatStateOf(0.25f),
            mutableFloatStateOf(0.7f),
            mutableFloatStateOf(0.4f),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Perform", style = MaterialTheme.typography.titleMedium)
        Text(
            "Macros + notes — machine-specific readout in a later phase",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text("Notes", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                48 to "C3", 52 to "E3", 55 to "G3",
                60 to "C4", 64 to "E4", 67 to "G4", 72 to "C5",
            ).forEach { (n, label) ->
                Button(
                    onClick = { onNoteOn(n) },
                    modifier = Modifier
                        .size(width = 52.dp, height = 48.dp)
                        .semantics { contentDescription = "Play $label" },
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Button(
            onClick = onNoteOffAll,
            modifier = Modifier.semantics { contentDescription = "All notes off" },
        ) { Text("All Notes Off") }

        Spacer(Modifier.height(4.dp))
        Text("Macros", style = MaterialTheme.typography.labelLarge)
        macroLabels.forEachIndexed { index, label ->
            if (index >= macroValues.size) return@forEachIndexed
            var value by macroValues[index]
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "$label: ${"%.2f".format(value)}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Slider(
                    value = value,
                    onValueChange = {
                        value = it
                        onMacro(index, it)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = label },
                )
            }
        }
    }
}
