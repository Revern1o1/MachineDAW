package com.justcode.machinedaw.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justcode.machinedaw.audio.AudioEngineBridge
import com.justcode.machinedaw.model.AvailableMachineTypes
import com.justcode.machinedaw.model.DawShellUiState
import com.justcode.machinedaw.model.DefaultMacroMaps
import com.justcode.machinedaw.model.MachineColorPalette
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineParamCatalog
import com.justcode.machinedaw.model.MachinePreset
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.model.MachineTypeInfo
import com.justcode.machinedaw.model.ParamMacroRoute
import com.justcode.machinedaw.model.PresetLibrary
import com.justcode.machinedaw.model.TransportUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DawShellViewModel : ViewModel() {

    private val _state = MutableStateFlow(DawShellUiState())
    val state: StateFlow<DawShellUiState> = _state.asStateFlow()

    private var meterJob: Job? = null
    private var nextColorIndex = 0

    fun startEngine() {
        if (!AudioEngineBridge.nativeStart()) return
        _state.update {
            it.copy(
                transport = it.transport.copy(
                    isEngineRunning = true,
                    isPlaying = true,
                    sampleRate = AudioEngineBridge.nativeGetSampleRate(),
                    bpm = AudioEngineBridge.nativeGetBpm(),
                )
            )
        }
        startMeterPolling()
    }

    fun stopEngine() {
        meterJob?.cancel()
        meterJob = null
        AudioEngineBridge.nativeStop()
        _state.update {
            it.copy(
                transport = TransportUiState(),
                tabs = emptyList(),
                selectedTabIndex = -1,
                meters = FloatArray(14),
            )
        }
        nextColorIndex = 0
    }

    fun togglePlay() {
        val playing = !_state.value.transport.isPlaying
        AudioEngineBridge.nativeSetTransportState(playing)
        _state.update { it.copy(transport = it.transport.copy(isPlaying = playing)) }
    }

    fun setBpm(bpm: Float) {
        val clamped = bpm.coerceIn(40f, 300f)
        AudioEngineBridge.nativeSetBpm(clamped)
        _state.update { it.copy(transport = it.transport.copy(bpm = clamped)) }
    }

    fun openMachinePicker() {
        if (_state.value.canAddMachine) _state.update { it.copy(showMachinePicker = true) }
    }

    fun dismissMachinePicker() { _state.update { it.copy(showMachinePicker = false) } }

    fun addMachine(type: MachineTypeInfo) {
        if (!_state.value.transport.isEngineRunning) startEngine()
        if (!_state.value.canAddMachine) return
        val id = AudioEngineBridge.nativeAddMachine(type.typeIndex)
        if (id < 0) return
        val color = MachineColorPalette[nextColorIndex % MachineColorPalette.size]
        nextColorIndex++
        val defaultPreset = PresetLibrary.defaultFor(type.typeId)
        val tab = MachineTab(
            engineId = id,
            typeIndex = type.typeIndex,
            typeId = type.typeId,
            displayName = type.displayName,
            color = color,
            presetId = defaultPreset?.id,
            presetName = defaultPreset?.name ?: "Default",
            macroRoutes = DefaultMacroMaps.forType(type.typeId),
        )
        _state.update {
            val tabs = it.tabs + tab
            it.copy(tabs = tabs, selectedTabIndex = tabs.lastIndex, showMachinePicker = false)
        }
        if (defaultPreset != null) applyPresetParams(id, defaultPreset)
    }

    fun openPresetBrowser() {
        if (_state.value.selectedTab != null) _state.update { it.copy(showPresetBrowser = true) }
    }

    fun dismissPresetBrowser() { _state.update { it.copy(showPresetBrowser = false) } }

    fun loadPreset(preset: MachinePreset) {
        val tab = _state.value.selectedTab ?: return
        if (preset.typeId != tab.typeId) return
        applyPresetParams(tab.engineId, preset)
        _state.update { s ->
            val idx = s.selectedTabIndex
            if (idx !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            tabs[idx] = tabs[idx].copy(presetId = preset.id, presetName = preset.name)
            s.copy(tabs = tabs, showPresetBrowser = false)
        }
    }

    fun cyclePresetNext() {
        val tab = _state.value.selectedTab ?: return
        val next = PresetLibrary.cycleNext(tab.typeId, tab.presetId) ?: return
        loadPreset(next)
    }

    fun cyclePresetPrev() {
        val tab = _state.value.selectedTab ?: return
        val prev = PresetLibrary.cyclePrev(tab.typeId, tab.presetId) ?: return
        loadPreset(prev)
    }

    fun presetsForSelected(): List<MachinePreset> {
        val typeId = _state.value.selectedTab?.typeId ?: return emptyList()
        return PresetLibrary.factoryFor(typeId)
    }

    private fun applyPresetParams(machineId: Int, preset: MachinePreset) {
        preset.params.forEachIndexed { paramId, value ->
            AudioEngineBridge.nativeSetParam(machineId, paramId, value)
        }
    }

    fun selectTab(index: Int) {
        if (index in _state.value.tabs.indices)
            _state.update { it.copy(selectedTabIndex = index, showTabSwitcher = false) }
    }

    fun toggleMute(index: Int) {
        _state.update { s ->
            if (index !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            val t = tabs[index]
            val muted = !t.isMuted
            tabs[index] = t.copy(isMuted = muted)
            AudioEngineBridge.nativeSetMute(t.engineId, muted)
            s.copy(tabs = tabs)
        }
    }

    fun renameTab(index: Int, name: String) {
        val trimmed = name.trim().ifEmpty { return }
        _state.update { s ->
            if (index !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            tabs[index] = tabs[index].copy(displayName = trimmed)
            s.copy(tabs = tabs)
        }
    }

    fun deleteTab(index: Int) {
        val s = _state.value
        if (index !in s.tabs.indices) return
        AudioEngineBridge.nativeRemoveMachine(s.tabs[index].engineId)
        _state.update {
            val tabs = it.tabs.toMutableList().also { list -> list.removeAt(index) }
            val newSelected = when {
                tabs.isEmpty() -> -1
                it.selectedTabIndex > index -> it.selectedTabIndex - 1
                it.selectedTabIndex == index -> index.coerceAtMost(tabs.lastIndex)
                else -> it.selectedTabIndex
            }
            it.copy(tabs = tabs, selectedTabIndex = newSelected, showTabSwitcher = false)
        }
    }

    fun reorderTabs(from: Int, to: Int) {
        _state.update { s ->
            if (from !in s.tabs.indices || to !in s.tabs.indices || from == to) return@update s
            val tabs = s.tabs.toMutableList()
            val item = tabs.removeAt(from)
            tabs.add(to, item)
            val selected = if (s.selectedTabIndex == from) to else s.selectedTabIndex
            s.copy(tabs = tabs, selectedTabIndex = selected)
        }
    }

    fun setLayer(layer: MachineLayer) {
        _state.update { s ->
            val idx = s.selectedTabIndex
            if (idx !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            tabs[idx] = tabs[idx].copy(activeLayer = layer)
            s.copy(tabs = tabs)
        }
    }

    fun openTabSwitcher() { _state.update { it.copy(showTabSwitcher = true) } }
    fun dismissTabSwitcher() { _state.update { it.copy(showTabSwitcher = false) } }

    fun noteOn(note: Int, velocity: Float = 0.9f) {
        val tab = _state.value.selectedTab ?: return
        AudioEngineBridge.nativeNoteOn(tab.engineId, note, velocity)
    }

    fun noteOff(note: Int) {
        val tab = _state.value.selectedTab ?: return
        AudioEngineBridge.nativeNoteOff(tab.engineId, note)
    }

    fun setParam(paramId: Int, value: Float) {
        val tab = _state.value.selectedTab ?: return
        AudioEngineBridge.nativeSetParam(tab.engineId, paramId, value)
    }

    fun setMacro(index: Int, value: Float) {
        val tab = _state.value.selectedTab ?: return
        val v = value.coerceIn(0f, 1f)
        AudioEngineBridge.nativeSetMacro(tab.engineId, index, v)
        tab.macroRoutes.filter { it.macroIndex == index }.forEach { route ->
            val paramVal = route.rangeMin + v * (route.rangeMax - route.rangeMin)
            AudioEngineBridge.nativeSetParam(tab.engineId, route.paramId, paramVal)
        }
    }

    fun mapParamToMacro(paramId: Int, macroIndex: Int) {
        val tab = _state.value.selectedTab ?: return
        val def = MachineParamCatalog.paramsFor(tab.typeId).find { it.id == paramId } ?: return
        val route = ParamMacroRoute(
            paramId = paramId,
            macroIndex = macroIndex.coerceIn(0, 3),
            rangeMin = def.min,
            rangeMax = def.max,
        )
        _state.update { s ->
            val idx = s.selectedTabIndex
            if (idx !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            val t = tabs[idx]
            val routes = t.macroRoutes.filterNot { it.paramId == paramId } + route
            tabs[idx] = t.copy(macroRoutes = routes)
            s.copy(tabs = tabs)
        }
    }

    fun clearParamMacro(paramId: Int) {
        _state.update { s ->
            val idx = s.selectedTabIndex
            if (idx !in s.tabs.indices) return@update s
            val tabs = s.tabs.toMutableList()
            val t = tabs[idx]
            tabs[idx] = t.copy(macroRoutes = t.macroRoutes.filterNot { it.paramId == paramId })
            s.copy(tabs = tabs)
        }
    }

    fun setPatternStep(bank: Int, step: Int, active: Boolean, note: Int = 60) {
        val tab = _state.value.selectedTab ?: return
        AudioEngineBridge.nativeSetPatternStep(
            tab.engineId, bank, step, note, if (active) 0.9f else 0f,
        )
    }

    fun setActivePattern(bank: Int) {
        val tab = _state.value.selectedTab ?: return
        AudioEngineBridge.nativeSetActivePattern(tab.engineId, bank)
    }

    fun machineTypes(): List<MachineTypeInfo> = AvailableMachineTypes

    private fun startMeterPolling() {
        meterJob?.cancel()
        meterJob = viewModelScope.launch {
            while (isActive) {
                val meters = AudioEngineBridge.nativeGetMeters()
                val playing = AudioEngineBridge.nativeIsPlaying()
                val sr = AudioEngineBridge.nativeGetSampleRate()
                val bpm = AudioEngineBridge.nativeGetBpm()
                val step = AudioEngineBridge.nativeGetCurrentStep()
                val bbt = AudioEngineBridge.nativeGetBbt()
                _state.update {
                    it.copy(
                        meters = meters,
                        transport = it.transport.copy(
                            isPlaying = playing,
                            sampleRate = sr,
                            isEngineRunning = AudioEngineBridge.nativeIsRunning(),
                            bpm = bpm,
                            currentStep = step,
                            bar = bbt.getOrElse(0) { 0 },
                            beat = bbt.getOrElse(1) { 0 },
                            tick = bbt.getOrElse(2) { 0 },
                        )
                    )
                }
                delay(40)
            }
        }
    }

    override fun onCleared() {
        meterJob?.cancel()
        AudioEngineBridge.nativeStop()
        super.onCleared()
    }
}
