package com.justcode.machinedaw.ui.shell

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.justcode.machinedaw.model.MachineLayer
import com.justcode.machinedaw.model.MachineTab
import com.justcode.machinedaw.ui.shell.layers.PerformLayer
import com.justcode.machinedaw.ui.shell.layers.ShapeLayer
import com.justcode.machinedaw.ui.shell.layers.WriteLayer

@Composable
fun MachineLayerHost(
    tab: MachineTab,
    currentStep: Int,
    isPlaying: Boolean,
    onLayerChange: (MachineLayer) -> Unit,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    onMacro: (Int, Float) -> Unit,
    onParam: (Int, Float) -> Unit,
    onSetStep: (bank: Int, step: Int, active: Boolean) -> Unit,
    onSelectBank: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = MachineLayer.entries
    val initial = pages.indexOf(tab.activeLayer).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initial, pageCount = { pages.size })

    LaunchedEffect(tab.activeLayer) {
        val target = pages.indexOf(tab.activeLayer).coerceAtLeast(0)
        if (pagerState.currentPage != target) pagerState.animateScrollToPage(target)
    }
    LaunchedEffect(pagerState.currentPage) {
        val layer = pages[pagerState.currentPage]
        if (layer != tab.activeLayer) onLayerChange(layer)
    }

    HorizontalPager(state = pagerState, modifier = modifier.fillMaxSize(), key = { pages[it] }) { page ->
        when (pages[page]) {
            MachineLayer.Perform -> PerformLayer(
                tab = tab, onNoteOn = onNoteOn, onNoteOff = onNoteOff, onMacro = onMacro,
                modifier = Modifier.fillMaxSize(),
            )
            MachineLayer.Shape -> ShapeLayer(
                tab = tab, onParam = onParam, modifier = Modifier.fillMaxSize(),
            )
            MachineLayer.Write -> WriteLayer(
                tab = tab, currentStep = currentStep, isPlaying = isPlaying,
                onSetStep = onSetStep, onSelectBank = onSelectBank,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
