package com.justcode.machinedaw.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Always-dark studio theme (UI kit). Light mode is out of scope.
 * Type target: IBM Plex Sans + Mono (system fallback until assets ship).
 */
private val StudioColors = darkColorScheme(
    primary = MachineColors.Azure,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = MachineColors.Surf2,
    onPrimaryContainer = MachineColors.Ink,
    secondary = MachineColors.Fx,
    onSecondary = MachineColors.Bg,
    secondaryContainer = MachineColors.Surf,
    onSecondaryContainer = MachineColors.Ink,
    tertiary = MachineColors.Play,
    onTertiary = MachineColors.Bg,
    background = MachineColors.Bg,
    onBackground = MachineColors.Ink,
    surface = MachineColors.Surf,
    onSurface = MachineColors.Ink,
    surfaceVariant = MachineColors.Surf2,
    onSurfaceVariant = MachineColors.Ink2,
    outline = MachineColors.Line,
    outlineVariant = MachineColors.Line,
    error = MachineColors.Rec,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF3B1515),
    onErrorContainer = MachineColors.Rec,
)

private val StudioTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp,
    ),
)

@Composable
fun MachineDAWTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StudioColors,
        typography = StudioTypography,
        content = content,
    )
}
