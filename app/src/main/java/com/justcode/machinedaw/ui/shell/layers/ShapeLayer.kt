package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.model.ParamKindUi
import com.justcode.machinedaw.model.ParamMacroRoute
import com.justcode.machinedaw.model.ParamUiDef
import com.justcode.machinedaw.model.PresetLibrary
import com.justcode.machinedaw.ui.theme.MachineColors

@Composable
fun ShapeLayer(
    tab: MachineTab,
    macroRoutes: List<ParamMacroRoute>,
    onParam: (Int, Float) -> Unit,
    onMapParamToMacro: (paramId: Int, macroIndex: Int) -> Unit,
    onClearParamMacro: (paramId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val defs = remember(tab.typeId) { MachineParamCatalog.paramsFor(tab.typeId) }
    val macroLabels = remember(tab.typeId) { MachineParamCatalog.macrosFor(tab.typeId) }
    val seed = remember(tab.presetId, tab.typeId) {
        tab.presetId?.let { PresetLibrary.find(tab.typeId, it)?.params }
    }
    val values = remember(tab.engineId, tab.presetId, defs) {
        defs.associate { def ->
            val initial = seed?.getOrNull(def.id) ?: def.default
            def.id to mutableFloatStateOf(initial)
        }
    }
    var mappingTarget by remember { mutableStateOf<ParamUiDef?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Shape", style = MaterialTheme.typography.titleMedium)
        Text(
            "Long-press a param to map to a Perform macro",
            style = MaterialTheme.typography.bodySmall,
            color = MachineColors.Ink2,
        )
        if (defs.isEmpty()) {
            Text("No params for this machine type", style = MaterialTheme.typography.bodyMedium)
            return
        }
        val routeByParam = remember(macroRoutes) { macroRoutes.associateBy { it.paramId } }
        val groups = defs.groupBy { it.group }
        groups.forEach { (group, params) ->
            Text(
                group,
                style = MaterialTheme.typography.titleSmall,
                color = tab.color,
                modifier = Modifier.padding(top = 8.dp),
            )
            params.forEach { def ->
                val state = values[def.id] ?: return@forEach
                var value by state
                val route = routeByParam[def.id]
                ParamRow(
                    def = def,
                    value = value,
                    mappedMacroLabel = route?.let { macroLabels.getOrNull(it.macroIndex) },
                    onChange = {
                        value = it
                        onParam(def.id, it)
                    },
                    onLongPress = { mappingTarget = def },
                )
            }
        }
    }

    mappingTarget?.let { def ->
        MacroMapDialog(
            paramName = def.name,
            macroLabels = macroLabels,
            currentMacro = macroRoutes.find { it.paramId == def.id }?.macroIndex,
            onPick = { index ->
                onMapParamToMacro(def.id, index)
                mappingTarget = null
            },
            onClear = {
                onClearParamMacro(def.id)
                mappingTarget = null
            },
            onDismiss = { mappingTarget = null },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ParamRow(
    def: ParamUiDef,
    value: Float,
    mappedMacroLabel: String?,
    onChange: (Float) -> Unit,
    onLongPress: () -> Unit,
) {
    val display = when {
        def.kind == ParamKindUi.Discrete && def.max <= 1f ->
            if (value >= 0.5f) "Square" else "Saw"
        def.unit.isNotEmpty() -> "${"%.0f".format(value)} ${def.unit}"
        else -> "%.2f".format(value)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
            .semantics {
                contentDescription = buildString {
                    append(def.name)
                    if (mappedMacroLabel != null) append(" mapped to $mappedMacroLabel")
                }
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "${def.name}: $display",
                style = MaterialTheme.typography.bodySmall,
                color = MachineColors.Ink,
            )
            if (mappedMacroLabel != null) {
                Text(
                    "→ $mappedMacroLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = MachineColors.Fx,
                )
            }
        }
        Slider(
            value = value.coerceIn(def.min, def.max),
            onValueChange = onChange,
            valueRange = def.min..def.max,
            steps = if (def.kind == ParamKindUi.Discrete) 1 else 0,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MacroMapDialog(
    paramName: String,
    macroLabels: List<String>,
    currentMacro: Int?,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Map \"$paramName\"") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Assign to a Perform macro. Moving that macro will drive this param.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MachineColors.Ink2,
                )
                macroLabels.forEachIndexed { index, label ->
                    TextButton(
                        onClick = { onPick(index) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (currentMacro == index) "● $label (current)" else label,
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (currentMacro != null) {
                TextButton(onClick = onClear) { Text("Clear mapping") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
