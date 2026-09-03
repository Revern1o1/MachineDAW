package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineTab

@Composable
fun WriteLayer(
    tab: MachineTab,
    modifier: Modifier = Modifier,
) {
    val patterns = listOf("A", "B", "C", "D", "E", "F", "G", "H")
    var activePattern by remember(tab.engineId) { mutableStateOf("A") }
    val steps = remember(tab.engineId, activePattern) {
        MutableList(16) { mutableStateOf(false) }
    }
    var recordArmed by remember(tab.engineId) { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Write", style = MaterialTheme.typography.titleMedium)
        Text(
            "Pattern bank + step grid (local) — engine clock in a later phase",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            patterns.forEach { letter ->
                FilterChip(
                    selected = activePattern == letter,
                    onClick = { activePattern = letter },
                    label = { Text(letter) },
                    modifier = Modifier.semantics { contentDescription = "Pattern $letter" },
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilterChip(
                selected = recordArmed,
                onClick = { recordArmed = !recordArmed },
                label = { Text(if (recordArmed) "Rec armed" else "Rec") },
            )
            Text("Pattern $activePattern", style = MaterialTheme.typography.labelLarge)
        }

        Text("16 steps", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            steps.forEachIndexed { index, state ->
                var on by state
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (on) tab.color
                            else MaterialTheme.colorScheme.surfaceVariant,
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .clickable { on = !on }
                        .semantics {
                            contentDescription = "Step ${index + 1} ${if (on) "on" else "off"}"
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (on) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Piano roll / drum sequencer UI arrives with pattern-clock JNI",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
