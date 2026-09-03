package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.TransportUiState

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
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            "Machine DAW",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        if (!state.isEngineRunning) {
            FilledTonalButton(
                onClick = onStartEngine,
                modifier = Modifier.semantics { contentDescription = "Start engine" },
            ) { Text("Start") }
        } else {
            TextButton(
                onClick = onStopEngine,
                modifier = Modifier.semantics { contentDescription = "Stop engine" },
            ) { Text("Stop") }

            FilledTonalButton(
                onClick = onTogglePlay,
                modifier = Modifier.semantics {
                    contentDescription = if (state.isPlaying) "Pause" else "Play"
                },
            ) { Text(if (state.isPlaying) "Pause" else "Play") }

            Text(
                "${state.bpm.toInt()} BPM",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                "${state.sampleRate} Hz",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(48.dp)
                .height(12.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(2.dp),
                )
                .semantics { contentDescription = "Master meter" },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(masterPeak.coerceIn(0f, 1f))
                    .height(12.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(2.dp),
                    )
                    .align(Alignment.CenterStart),
            )
        }
    }
}
