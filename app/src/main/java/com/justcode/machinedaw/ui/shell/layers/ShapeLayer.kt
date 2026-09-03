package com.justcode.machinedaw.ui.shell.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.model.ParamKindUi
import com.justcode.machinedaw.model.ParamUiDef

@Composable
fun ShapeLayer(
    tab: MachineTab,
    onParam: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val defs = remember(tab.typeId) { MachineParamCatalog.paramsFor(tab.typeId) }
    val values = remember(tab.engineId, defs) {
        defs.associate { it.id to mutableFloatStateOf(it.default) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Shape", style = MaterialTheme.typography.titleMedium)
        Text(
            "Full engine params — long-press map-to-macro later",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (defs.isEmpty()) {
            Text("No params for this machine type", style = MaterialTheme.typography.bodyMedium)
            return
        }

        val groups = defs.groupBy { it.group }
        groups.forEach { (group, params) ->
            Text(
                group,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
            params.forEach { def ->
                val state = values[def.id] ?: return@forEach
                var value by state
                ParamRow(
                    def = def,
                    value = value,
                    onChange = {
                        value = it
                        onParam(def.id, it)
                    },
                )
            }
        }
    }
}

@Composable
private fun ParamRow(
    def: ParamUiDef,
    value: Float,
    onChange: (Float) -> Unit,
) {
    val display = when {
        def.kind == ParamKindUi.Discrete && def.max <= 1f ->
            if (value >= 0.5f) "Square" else "Saw"
        def.unit.isNotEmpty() -> "${"%.0f".format(value)} ${def.unit}"
        else -> "%.2f".format(value)
    }
    Column(Modifier.fillMaxWidth()) {
        Text("${def.name}: $display", style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value.coerceIn(def.min, def.max),
            onValueChange = onChange,
            valueRange = def.min..def.max,
            steps = if (def.kind == ParamKindUi.Discrete) 1 else 0,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = def.name },
        )
    }
}
