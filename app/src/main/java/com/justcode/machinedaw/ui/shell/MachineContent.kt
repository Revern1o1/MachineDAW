package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab

@Composable
fun MachineContent(
    tab: MachineTab,
    onNoteOn: (Int) -> Unit,
    onNoteOffAll: () -> Unit,
    onMacro: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (tab.activeLayer) {
        MachineLayer.Perform -> PerformPlaceholder(
            tab = tab,
            onNoteOn = onNoteOn,
            onNoteOffAll = onNoteOffAll,
            onMacro = onMacro,
            modifier = modifier,
        )
        MachineLayer.Shape -> LayerStub(
            title = "Shape",
            subtitle = "Full param editor lands in Phase 4",
            modifier = modifier,
        )
        MachineLayer.Write -> LayerStub(
            title = "Write",
            subtitle = "Sequencer / piano roll lands in Phase 4",
            modifier = modifier,
        )
    }
}

@Composable
private fun PerformPlaceholder(
    tab: MachineTab,
    onNoteOn: (Int) -> Unit,
    onNoteOffAll: () -> Unit,
    onMacro: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var m0 by remember(tab.engineId) { mutableFloatStateOf(0.5f) }
    var m1 by remember(tab.engineId) { mutableFloatStateOf(0.25f) }
    var m2 by remember(tab.engineId) { mutableFloatStateOf(0.7f) }
    var m3 by remember(tab.engineId) { mutableFloatStateOf(0.4f) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Perform", style = MaterialTheme.typography.titleMedium)
        Text(
            "Quick play + macros — machine-specific UI in Phase 4+",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text("Notes", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(48 to "C3", 52 to "E3", 55 to "G3", 60 to "C4", 64 to "E4", 67 to "G4").forEach { (n, label) ->
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
        Button(onClick = onNoteOffAll) { Text("All Notes Off") }

        Spacer(Modifier.height(8.dp))
        Text("Macros", style = MaterialTheme.typography.labelLarge)
        MacroRow("Macro 0", m0) { m0 = it; onMacro(0, it) }
        MacroRow("Macro 1", m1) { m1 = it; onMacro(1, it) }
        MacroRow("Macro 2", m2) { m2 = it; onMacro(2, it) }
        MacroRow("Macro 3", m3) { m3 = it; onMacro(3, it) }
    }
}

@Composable
private fun MacroRow(label: String, value: Float, onChange: (Float) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text("$label: ${"%.2f".format(value)}", style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
        )
    }
}

@Composable
private fun LayerStub(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
