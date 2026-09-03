package com.justcode.machinedaw

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.justcode.machinedaw.audio.AudioEngineBridge
import com.justcode.machinedaw.ui.theme.MachineDAWTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            MachineDAWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Phase2TestScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        AudioEngineBridge.nativeStop()
        super.onDestroy()
    }
}

@Composable
fun Phase2TestScreen(modifier: Modifier = Modifier) {
    var isRunning by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var sampleRate by remember { mutableIntStateOf(0) }
    var machineCount by remember { mutableIntStateOf(0) }
    var activeMachineId by remember { mutableIntStateOf(-1) }
    val peakMeters = remember { FloatArray(14) }
    var meterTick by remember { mutableIntStateOf(0) }

    var macro0 by remember { mutableFloatStateOf(0.5f) }
    var macro1 by remember { mutableFloatStateOf(0.25f) }
    var macro2 by remember { mutableFloatStateOf(0.7f) }
    var macro3 by remember { mutableFloatStateOf(0.4f) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            isPlaying = AudioEngineBridge.nativeIsPlaying()
            sampleRate = AudioEngineBridge.nativeGetSampleRate()
            machineCount = AudioEngineBridge.nativeMachineCount()
            val meters = AudioEngineBridge.nativeGetMeters()
            val n = minOf(meters.size, peakMeters.size)
            for (i in 0 until n) peakMeters[i] = meters[i]
            for (i in n until peakMeters.size) peakMeters[i] = 0f
            meterTick++
            delay(50)
        }
    }

    DisposableEffect(Unit) {
        onDispose { AudioEngineBridge.nativeStop() }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Machine DAW - Phase 2", style = MaterialTheme.typography.headlineSmall)
        Text(
            "MachineRegistry + multi-machine rack",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (AudioEngineBridge.nativeStart()) {
                        isRunning = true
                        isPlaying = true
                    }
                },
                enabled = !isRunning
            ) { Text("Start Engine") }

            Button(
                onClick = {
                    AudioEngineBridge.nativeStop()
                    isRunning = false
                    isPlaying = false
                    activeMachineId = -1
                    machineCount = 0
                },
                enabled = isRunning
            ) { Text("Stop") }

            Button(
                onClick = {
                    val next = !isPlaying
                    AudioEngineBridge.nativeSetTransportState(next)
                    isPlaying = next
                },
                enabled = isRunning
            ) { Text(if (isPlaying) "Pause" else "Play") }
        }

        Text(
            if (isRunning) "Running @ $sampleRate Hz  |  machines: $machineCount"
            else "Engine stopped",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Add machine", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val id = AudioEngineBridge.nativeAddMachine(AudioEngineBridge.TYPE_SINE_TEST)
                    if (id >= 0) {
                        activeMachineId = id
                        machineCount = AudioEngineBridge.nativeMachineCount()
                    }
                },
                enabled = isRunning && machineCount < 14
            ) { Text("+ SineTest") }

            Button(
                onClick = {
                    val id = AudioEngineBridge.nativeAddMachine(AudioEngineBridge.TYPE_SUBSYNTH)
                    if (id >= 0) {
                        activeMachineId = id
                        machineCount = AudioEngineBridge.nativeMachineCount()
                    }
                },
                enabled = isRunning && machineCount < 14
            ) { Text("+ Subsynth") }
        }

        if (activeMachineId >= 0) {
            Text("Active machine id: $activeMachineId", style = MaterialTheme.typography.bodyMedium)

            Text("Notes", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val notes = listOf(
                    48 to "C3", 52 to "E3", 55 to "G3",
                    60 to "C4", 64 to "E4", 67 to "G4", 72 to "C5"
                )
                notes.forEach { (note, label) ->
                    Button(
                        onClick = {
                            AudioEngineBridge.nativeNoteOn(activeMachineId, note, 0.9f)
                        },
                        modifier = Modifier
                            .size(width = 52.dp, height = 48.dp)
                            .semantics { contentDescription = "Play note $label" }
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Button(
                onClick = {
                    for (n in 36..84) {
                        AudioEngineBridge.nativeNoteOff(activeMachineId, n)
                    }
                },
                modifier = Modifier.semantics { contentDescription = "Release all notes" }
            ) { Text("All Notes Off") }

            Text("Macros (Perform)", style = MaterialTheme.typography.titleMedium)
            MacroSlider(
                label = "Macro 0",
                value = macro0,
                onChange = {
                    macro0 = it
                    AudioEngineBridge.nativeSetMacro(activeMachineId, 0, it)
                }
            )
            MacroSlider(
                label = "Macro 1",
                value = macro1,
                onChange = {
                    macro1 = it
                    AudioEngineBridge.nativeSetMacro(activeMachineId, 1, it)
                }
            )
            MacroSlider(
                label = "Macro 2",
                value = macro2,
                onChange = {
                    macro2 = it
                    AudioEngineBridge.nativeSetMacro(activeMachineId, 2, it)
                }
            )
            MacroSlider(
                label = "Macro 3",
                value = macro3,
                onChange = {
                    macro3 = it
                    AudioEngineBridge.nativeSetMacro(activeMachineId, 3, it)
                }
            )
        }

        @Suppress("UNUSED_EXPRESSION")
        meterTick

        if (machineCount > 0) {
            Text("Meters", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .semantics { contentDescription = "Machine level meters" },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0 until machineCount) {
                    val peak = peakMeters[i].coerceIn(0f, 1f)
                    val h = (peak * 32f).dp
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(h.coerceAtLeast(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .semantics {
                                contentDescription =
                                    "Machine ${i + 1} level ${(peak * 100).toInt()} percent"
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroSlider(
    label: String,
    value: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("$label: ${"%.2f".format(value)}", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "$label value" }
        )
    }
}
