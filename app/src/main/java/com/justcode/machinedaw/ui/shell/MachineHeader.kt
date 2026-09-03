package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab

@Composable
fun MachineHeader(
    tab: MachineTab,
    onLayerChange: (MachineLayer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            modifier = Modifier.weight(1f),
        )

        Text(
            "Default",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .semantics { contentDescription = "Preset: Default" },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            MachineLayer.entries.forEach { layer ->
                FilterChip(
                    selected = tab.activeLayer == layer,
                    onClick = { onLayerChange(layer) },
                    label = {
                        Text(layer.name, style = MaterialTheme.typography.labelSmall)
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "${layer.name} layer"
                    },
                )
            }
        }
    }
}
