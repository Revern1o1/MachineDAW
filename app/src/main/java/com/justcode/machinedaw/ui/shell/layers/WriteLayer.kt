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
import androidx.compose.runtime.mutableStateListOf
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
import com.justcode.machinedaw.ui.theme.MachineColors

/**
 * Write — pattern bank A–H + 16-step grid wired to native pattern clock.
 * UI holds a local mirror of step on/off; engine is the canonical sequencer.
 */
@Composable
fun WriteLayer(
    tab: MachineTab,
    currentStep: Int,
    isPlaying: Boolean,
    onSetStep: (bank: Int, step: Int, active: Boolean) -> Unit,
    onSelectBank: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val patterns = listOf("A", "B", "C", "D", "E", "F", "G", "H")
    var activeBank by remember(tab.engineId) { mutableStateOf(0) }
    // Local mirror only — engine owns the real pattern (no read-back yet)
    val steps = remember(tab.engineId, activeBank) {
        mutableStateListOf(*BooleanArray(16) { false }.toTypedArray())
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Write", style = MaterialTheme.typography.titleMedium)
        Text(
            "16 steps · engine clock · C4 hits",
            style = MaterialTheme.typography.bodySmall,
            color = MachineColors.Ink2,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            patterns.forEachIndexed { index, letter ->
                FilterChip(
                    selected = activeBank == index,
                    onClick = {
                        activeBank = index
                        onSelectBank(index)
                    },
                    label = { Text(letter) },
                    modifier = Modifier.semantics { contentDescription = "Pattern $letter" },
                )
            }
        }

        Text(
            "Pattern ${patterns[activeBank]}  ·  step ${currentStep + 1}/16",
            style = MaterialTheme.typography.labelLarge,
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            steps.forEachIndexed { index, on ->
                val isPlayhead = isPlaying && currentStep == index
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isPlayhead && on -> MachineColors.Play
                                on -> tab.color
                                isPlayhead -> MachineColors.Play.copy(alpha = 0.35f)
                                else -> MachineColors.Surf2
                            },
                        )
                        .border(
                            1.dp,
                            if (isPlayhead) MachineColors.Play else MachineColors.Line,
                            RoundedCornerShape(6.dp),
                        )
                        .clickable {
                            val next = !on
                            steps[index] = next
                            onSetStep(activeBank, index, next)
                        }
                        .semantics {
                            contentDescription =
                                "Step ${index + 1} ${if (on) "on" else "off"}"
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (on || isPlayhead) MachineColors.Ink else MachineColors.Ink2,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Press Play — armed steps fire C4 via the native pattern clock",
            style = MaterialTheme.typography.bodySmall,
            color = MachineColors.Ink2,
        )
    }
}
