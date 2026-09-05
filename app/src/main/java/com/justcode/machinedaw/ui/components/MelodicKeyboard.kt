package com.justcode.machinedaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.ui.theme.MachineColors

private val DefaultWhiteKeys = listOf(
    48 to "C3", 50 to "D3", 52 to "E3", 53 to "F3", 55 to "G3", 57 to "A3", 59 to "B3",
    60 to "C4", 62 to "D4", 64 to "E4", 65 to "F4", 67 to "G4", 69 to "A4", 71 to "B4",
    72 to "C5",
)

/**
 * Docked steel/ink keyboard (kit V6). Hold = note on, release = note off.
 */
@Composable
fun MelodicKeyboard(
    accent: Color,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    modifier: Modifier = Modifier,
    keys: List<Pair<Int, String>> = DefaultWhiteKeys,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(MachineColors.Bg2)
            .border(1.dp, MachineColors.Line, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        keys.forEach { (note, label) ->
            var pressed by remember(note) { mutableStateOf(false) }
            val isC = label.startsWith("C")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            pressed -> accent
                            isC -> MachineColors.Ink.copy(alpha = 0.95f)
                            else -> MachineColors.Ink.copy(alpha = 0.82f)
                        },
                    )
                    .border(
                        width = 1.dp,
                        color = if (pressed) accent else MachineColors.Line,
                        shape = RoundedCornerShape(4.dp),
                    )
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
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pressed) MachineColors.Bg else MachineColors.Bg2,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
        }
    }
}
