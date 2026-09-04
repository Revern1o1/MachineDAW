package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justcode.machinedaw.model.TransportUiState
import com.justcode.machinedaw.ui.theme.MachineColors

@Composable
fun TransportBar(
    state: TransportUiState,
    masterPeak: Float,
    onStartEngine: () -> Unit,
    onStopEngine: () -> Unit,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(MachineColors.Bg2)
            .border(1.dp, MachineColors.Line)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TransportIconButton(label = "≡", contentDescription = "Project menu", onClick = { })

        Text(
            text = "MACHINE",
            style = MaterialTheme.typography.titleSmall,
            color = MachineColors.Ink,
            letterSpacing = 1.5.sp,
        )
        Text(text = "DAW", style = MaterialTheme.typography.labelMedium, color = MachineColors.Ink2)

        TransportCircleButton(
            contentDescription = "Stop",
            filled = false,
            accent = MachineColors.Ink2,
            onClick = onStopEngine,
            glyph = "■",
        )

        if (!state.isEngineRunning) {
            TransportCircleButton(
                contentDescription = "Start engine",
                filled = true,
                accent = MachineColors.Play,
                onClick = onStartEngine,
                glyph = "▶",
            )
        } else {
            TransportCircleButton(
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                filled = true,
                accent = if (state.isPlaying) MachineColors.Play else MachineColors.Ink2,
                onClick = onTogglePlay,
                glyph = "▶",
            )
        }

        TransportCircleButton(
            contentDescription = "Record",
            filled = true,
            accent = MachineColors.Rec.copy(alpha = 0.35f),
            onClick = { },
            glyph = "●",
            glyphColor = MachineColors.Rec,
        )

        Text(
            text = String.format("%.1f", state.bpm),
            style = MaterialTheme.typography.titleSmall,
            color = MachineColors.Ink,
            fontFamily = FontFamily.Monospace,
        )
        Text("BPM", style = MaterialTheme.typography.labelSmall, color = MachineColors.Ink2)

        Text(
            text = "04:02:768",
            style = MaterialTheme.typography.labelSmall,
            color = MachineColors.Ink2,
            fontFamily = FontFamily.Monospace,
        )

        Box(Modifier.weight(1f))

        Text(
            text = if (state.isEngineRunning) "Night Bus" else "untitled",
            style = MaterialTheme.typography.labelMedium,
            color = MachineColors.Ink2,
        )
        MeterPips(level = masterPeak.coerceIn(0f, 1f))
    }
}

@Composable
private fun TransportCircleButton(
    contentDescription: String,
    filled: Boolean,
    accent: Color,
    onClick: () -> Unit,
    glyph: String,
    glyphColor: Color = Color.White,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (filled) accent else Color.Transparent)
            .border(1.dp, if (filled) Color.Transparent else MachineColors.Line, CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(glyph, color = if (filled) glyphColor else accent, fontSize = 12.sp)
    }
}

@Composable
private fun TransportIconButton(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = MachineColors.Ink2, fontSize = 16.sp)
    }
}

@Composable
private fun MeterPips(level: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(16.dp),
    ) {
        repeat(8) { i ->
            val on = level > (i / 8f)
            Box(
                Modifier
                    .width(3.dp)
                    .fillMaxHeight(0.35f + i * 0.08f)
                    .background(
                        when {
                            !on -> MachineColors.Line
                            i >= 6 -> MachineColors.Rec
                            i >= 4 -> MachineColors.Amber
                            else -> MachineColors.Play
                        },
                        RoundedCornerShape(1.dp),
                    ),
            )
        }
    }
}
