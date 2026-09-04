package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.ui.theme.MachineColors

/**
 * Perform — macros + melodic keyboard (press/release → noteOn/noteOff).
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
    val macroValues = remember(tab.engineId) {
        listOf(
            mutableFloatStateOf(0.5f),
            mutableFloatStateOf(0.25f),
            mutableFloatStateOf(0.7f),
            mutableFloatStateOf(0.4f),
        )
    }

    val whiteKeys = listOf(
        48 to "C3", 50 to "D3", 52 to "E3", 53 to "F3", 55 to "G3", 57 to "A3", 59 to "B3",
        60 to "C4", 62 to "D4", 64 to "E4", 65 to "F4", 67 to "G4", 69 to "A4", 71 to "B4",
        72 to "C5",
    )

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Perform", style = MaterialTheme.typography.titleMedium)
            Text(
                "Macros + keyboard",
                style = MaterialTheme.typography.bodySmall,
                color = MachineColors.Ink2,
            )

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

        // Docked keyboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(MachineColors.Bg2)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            whiteKeys.forEach { (note, label) ->
                var pressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .background(
                            if (pressed) tab.color else MachineColors.Ink.copy(alpha = 0.92f),
                        )
                        .border(1.dp, MachineColors.Line)
                        .pointerInput(note) {
                            detectTapGestures(
                                onPress = {
                                    pressed = true
                                    onNoteOn(note)
                                    try {
                                        awaitRelease()
                                    } finally {
                                        pressed = false
                                        onNoteOff(note)
                                    }
                                },
                            )
                        }
                        .semantics { contentDescription = "Key $label" },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pressed) MachineColors.Ink else MachineColors.Bg,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }
    }
}
