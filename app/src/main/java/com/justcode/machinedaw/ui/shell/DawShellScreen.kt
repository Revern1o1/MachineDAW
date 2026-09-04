package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.justcode.machinedaw.model.DawShellUiState
import com.justcode.machinedaw.ui.theme.MachineColors
import com.justcode.machinedaw.viewmodel.DawShellViewModel

@Composable
fun DawShellScreen(
    viewModel: DawShellViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            TransportBar(
                state = state.transport,
                masterPeak = state.meters.take(state.tabs.size).maxOrNull() ?: 0f,
                onStartEngine = viewModel::startEngine,
                onStopEngine = viewModel::stopEngine,
                onTogglePlay = viewModel::togglePlay,
            )

            TabStrip(
                tabs = state.tabs,
                selectedIndex = state.selectedTabIndex,
                canAdd = state.canAddMachine && state.transport.isEngineRunning,
                onSelect = viewModel::selectTab,
                onToggleMute = viewModel::toggleMute,
                onDelete = viewModel::deleteTab,
                onAdd = viewModel::openMachinePicker,
                onOpenSwitcher = viewModel::openTabSwitcher,
            )

            val tab = state.selectedTab
            if (tab != null) {
                MachineHeader(tab = tab, onLayerChange = viewModel::setLayer)
                MachineContent(
                    tab = tab,
                    onLayerChange = viewModel::setLayer,
                    onNoteOn = { viewModel.noteOn(it) },
                    onNoteOffAll = { for (n in 36..84) viewModel.noteOff(n) },
                    onMacro = viewModel::setMacro,
                    onParam = viewModel::setParam,
                    modifier = Modifier.weight(1f),
                )
            } else {
                EmptyWorkspace(
                    engineRunning = state.transport.isEngineRunning,
                    onStart = viewModel::startEngine,
                    onAdd = viewModel::openMachinePicker,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (state.showMachinePicker) {
            MachinePickerSheet(
                types = viewModel.machineTypes(),
                onSelect = viewModel::addMachine,
                onDismiss = viewModel::dismissMachinePicker,
            )
        }

        if (state.showTabSwitcher) {
            TabSwitcherOverlay(
                state = state,
                onSelect = viewModel::selectTab,
                onDismiss = viewModel::dismissTabSwitcher,
            )
        }
    }
}

@Composable
private fun EmptyWorkspace(
    engineRunning: Boolean,
    onStart: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "MACHINE",
                style = MaterialTheme.typography.displayLarge,
                color = MachineColors.Ink,
                letterSpacing = 4.sp,
            )
            Text(
                "Add a machine to start",
                style = MaterialTheme.typography.titleMedium,
                color = MachineColors.Ink,
            )
            Text(
                "Synths, drums, pads — each opens as a tab with Perform, Shape and Write. Cap is 14.",
                style = MaterialTheme.typography.bodySmall,
                color = MachineColors.Ink2,
            )
            TextButton(
                onClick = if (engineRunning) onAdd else onStart,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MachineColors.Ink,
                    contentColor = MachineColors.Bg,
                ),
            ) {
                Text(if (engineRunning) "Browse machines" else "Start engine")
            }
        }
    }
}

@Composable
private fun TabSwitcherOverlay(
    state: DawShellUiState,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .padding(16.dp)
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Machines", style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(state.tabs, key = { _, t -> t.engineId }) { index, tab ->
                        Card(
                            onClick = { onSelect(index) },
                            colors = CardDefaults.cardColors(
                                containerColor = tab.color.copy(alpha = 0.2f),
                            ),
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(tab.displayName, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    tab.typeId,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}
