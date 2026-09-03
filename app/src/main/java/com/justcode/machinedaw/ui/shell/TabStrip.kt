package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineTab

@Composable
fun TabStrip(
    tabs: List<MachineTab>,
    selectedIndex: Int,
    canAdd: Boolean,
    onSelect: (Int) -> Unit,
    onToggleMute: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: () -> Unit,
    onOpenSwitcher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabs.forEachIndexed { index, tab ->
                MachineTabChip(
                    tab = tab,
                    selected = index == selectedIndex,
                    onSelect = { onSelect(index) },
                    onToggleMute = { onToggleMute(index) },
                    onDelete = { onDelete(index) },
                )
            }
            TextButton(
                onClick = onAdd,
                enabled = canAdd,
                modifier = Modifier
                    .semantics { contentDescription = "Add machine" }
                    .height(32.dp),
            ) { Text("+") }
        }

        if (tabs.isNotEmpty()) {
            TextButton(
                onClick = onOpenSwitcher,
                modifier = Modifier.semantics {
                    contentDescription = "Open tab switcher, ${tabs.size} machines"
                },
            ) { Text("${tabs.size}") }
        }

        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                .clickable { }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .semantics { contentDescription = "Mixer tab" },
        ) {
            Text("Mix", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MachineTabChip(
    tab: MachineTab,
    selected: Boolean,
    onSelect: () -> Unit,
    onToggleMute: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .widthIn(min = 72.dp, max = 140.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (selected) tab.color.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) tab.color else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onSelect() }
            .padding(horizontal = 6.dp)
            .semantics {
                contentDescription =
                    "${tab.displayName}${if (selected) ", selected" else ""}${if (tab.isMuted) ", muted" else ""}"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(tab.color),
        )
        Text(
            tab.displayName,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f, fill = false)
                .clickable { menuOpen = true },
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    if (tab.isMuted) Color(0xFFFF5252)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
                .clickable(onClick = onToggleMute)
                .semantics {
                    contentDescription = if (tab.isMuted) "Unmute" else "Mute"
                },
        )

        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    menuOpen = false
                    onDelete()
                },
            )
        }
    }
}
