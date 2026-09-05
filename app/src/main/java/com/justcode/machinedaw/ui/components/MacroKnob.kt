package com.justcode.machinedaw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.justcode.machinedaw.ui.theme.MachineColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Circular macro knob (kit V6 / component catalog).
 * Value 0..1; vertical drag.
 */
@Composable
fun MacroKnob(
    label: String,
    value: Float,
    accent: Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    val startAngle = 135f
    val sweepMax = 270f
    val clamped = value.coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.semantics { contentDescription = "$label ${"%.2f".format(clamped)}" },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        val delta = -drag.y / size.toPx()
                        onValueChange((clamped + delta).coerceIn(0f, 1f))
                    }
                },
        ) {
            Canvas(Modifier.size(size)) {
                val stroke = 6.dp.toPx()
                val pad = stroke / 2f + 2.dp.toPx()
                val diameter = size.toPx() - pad * 2f
                val topLeft = Offset(pad, pad)
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = MachineColors.Line,
                    startAngle = startAngle,
                    sweepAngle = sweepMax,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = accent,
                    startAngle = startAngle,
                    sweepAngle = sweepMax * clamped,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                val angleRad = Math.toRadians((startAngle + sweepMax * clamped).toDouble())
                val cx = size.toPx() / 2f
                val cy = size.toPx() / 2f
                val r = diameter / 2f
                val ix = cx + r * cos(angleRad).toFloat()
                val iy = cy + r * sin(angleRad).toFloat()
                drawCircle(color = accent, radius = 5.dp.toPx(), center = Offset(ix, iy))
                drawCircle(
                    color = MachineColors.Surf2,
                    radius = diameter * 0.28f,
                    center = Offset(cx, cy),
                )
            }
            Text(
                text = "%.0f".format(clamped * 100),
                style = MaterialTheme.typography.labelSmall,
                color = MachineColors.Ink,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MachineColors.Ink2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
        )
    }
}
