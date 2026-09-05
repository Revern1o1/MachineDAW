package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab

@Composable
fun MachineHeader(
    tab: MachineTab,
    onLayerChange: (MachineLayer) -> Unit,
    onPresetPrev: () -> Unit = {},
    onPresetNext: () -> Unit = {},
    onOpenPresetBrowser: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(tab.color),
        )
        Text(
            tab.displayName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 96.dp),
        )

        // Preset ◀ name ▶  (kit header)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 2.dp, vertical = 2.dp),
        ) {
            Text(
                text = "◀",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onPresetPrev)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .semantics { contentDescription = "Previous preset" },
            )
            Text(
                text = tab.presetName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .widthIn(min = 56.dp, max = 110.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onOpenPresetBrowser)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .semantics { contentDescription = "Preset: ${tab.presetName}. Open browser" },
            )
            Text(
                text = "▶",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onPresetNext)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .semantics { contentDescription = "Next preset" },
            )
        }

        // Layer switch
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            MachineLayer.entries.forEach { layer ->
                FilterChip(
                    selected = tab.activeLayer == layer,
                    onClick = { onLayerChange(layer) },
                    label = {
                        Text(
                            layer.name,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "${layer.name} layer"
                    },
                )
            }
        }
    }
}
