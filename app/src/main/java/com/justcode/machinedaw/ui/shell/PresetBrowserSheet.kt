package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachinePreset

/**
 * Preset browser (kit 09 spirit): Factory list first.
 * User / Favorites / Imported tabs can land when persistence exists.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetBrowserSheet(
    machineName: String,
    machineColor: Color,
    currentPresetId: String?,
    presets: List<MachinePreset>,
    onSelect: (MachinePreset) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(machineColor),
                )
                Text(
                    text = "$machineName presets",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = "Factory",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (presets.isEmpty()) {
                Text(
                    text = "No factory presets for this machine yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(presets, key = { it.id }) { preset ->
                        val selected = preset.id == currentPresetId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.surfaceVariant
                                    else Color.Transparent,
                                )
                                .clickable { onSelect(preset) }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                .semantics {
                                    contentDescription = "Preset ${preset.name}"
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (selected) machineColor
                                    else MaterialTheme.colorScheme.onSurface,
                                )
                                if (preset.category.isNotBlank()) {
                                    Text(
                                        text = preset.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            if (selected) {
                                Text(
                                    text = "Loaded",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = machineColor,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
