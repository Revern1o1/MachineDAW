package com.justcode.machinedaw.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Machine DAW design tokens — matches docs/ui-kit/00-design-system.png
 * Brand chrome stays steel/ink. Play green and Record red are semantic.
 * Machine hues are identity only (never the sole cue).
 */
object MachineColors {
    // Surfaces
    val Bg = Color(0xFF090B0D)
    val Bg2 = Color(0xFF0E1114)
    val Surf = Color(0xFF14191F)
    val Surf2 = Color(0xFF1A2129)
    val Ink = Color(0xFFE8ECF0)
    val Ink2 = Color(0xFF9AA3AD)
    val Line = Color(0xFF2A313A)

    // Semantic
    val Play = Color(0xFF30DC97)
    val Rec = Color(0xFFE24B4B)
    val Fx = Color(0xFF7EC8C3)

    // Machine palette (order = assignment order on create)
    val Azure = Color(0xFF3B82F6)
    val Rust = Color(0xFFE07A3D)
    val Emerald = Color(0xFF22C55E)
    val Indigo = Color(0xFF6366F1)
    val Amber = Color(0xFFF59E0B)
    val Cyan = Color(0xFF22D3EE)
    val Rose = Color(0xFFF472B6)
    val Olive = Color(0xFF84CC16)
    val Cornflower = Color(0xFF60A5FA)
    val Copper = Color(0xFFD97706)
    val Sea = Color(0xFF2DD4BF)
    val Steel = Color(0xFF94A3B8)
    val Slate = Color(0xFF64748B)
    val Coral = Color(0xFFFB7185)

    val MachinePalette: List<Color> = listOf(
        Azure, Rust, Emerald, Indigo, Amber, Cyan, Rose, Olive,
        Cornflower, Copper, Sea, Steel, Slate, Coral,
    )
}
