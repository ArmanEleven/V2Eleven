package com.dave_cli.proxybox.ui.main.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object C {
    val Background = Color(0xFF050507)
    val Surface = Color(0xFF12090B)
    val SurfaceVariant = Color(0xFF1C0D10)

    val Primary = Color(0xFFE50914)
    val PrimaryDark = Color(0xFF8B0000)
    val PrimaryGlow = Color(0xFFFF2633)

    val TextPrimary = Color(0xFFFFF5F5)
    val TextSecondary = Color(0xFFB8A5A5)
    val TextDim = Color(0xFF735F63)

    val Green = Color(0xFF4ADE80)
    val GreenDark = Color(0xFF28643F)
    val Red = Color(0xFFFF3344)
    val Yellow = Color(0xFFFACC15)
    val Blue = Color(0xFF60A5FA)
    val Amber = Color(0xFFF59E0B)
    val Pink = Color(0xFFF472B6)
    val Violet = Color(0xFFA78BFA)

    val Border = Color(0xFF351419)
    val Divider = Color(0xFF241013)

    val UpsideDownGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF050507),
            Color(0xFF160608),
            Color(0xFF30090D),
            Color(0xFF120608),
            Color(0xFF050507)
        )
    )

    val RedGlow = Brush.radialGradient(
        colors = listOf(
            Color(0x55FF0015),
            Color(0x220F0004),
            Color.Transparent
        )
    )
}

private val DarkScheme = darkColorScheme(
    primary = C.Primary,
    onPrimary = Color.White,
    surface = C.SurfaceVariant,
    onSurface = C.TextPrimary,
    surfaceVariant = C.Surface,
    onSurfaceVariant = C.TextPrimary,
    background = C.Background,
    onBackground = C.TextPrimary,
    outline = C.Border,
)

@Composable
fun ProxyBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content
    )
}
