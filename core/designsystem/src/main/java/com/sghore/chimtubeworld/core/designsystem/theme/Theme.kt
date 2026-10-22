package com.sghore.chimtubeworld.core.designsystem.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Black,
    primaryContainer = Black,
    onPrimary = White,
    onPrimaryContainer = White,
    secondary = White,
    secondaryContainer = White,
    onSecondary = Black,
    onSecondaryContainer = Black,
    tertiary = White,
    tertiaryContainer = White,
    onTertiary = Black,
    onTertiaryContainer = Black,
    primaryFixed = Black,
    primaryFixedDim = Black,
    onPrimaryFixed = White,
    onPrimaryFixedVariant = White,
    secondaryFixed = White,
    secondaryFixedDim = White,
    onSecondaryFixed = Black,
    onSecondaryFixedVariant = Black,
    tertiaryFixed = White,
    tertiaryFixedDim = White,
    onTertiaryFixed = Black,
    onTertiaryFixedVariant = Black,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = White.copy(alpha = 0.6f),
    surfaceTint = Color.Transparent,
    surfaceDim = DarkSurface,
    surfaceBright = DarkSurface,
    surfaceContainerLowest = DarkSurface,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurface,
    surfaceContainerHighest = DarkSurface,
    error = DarkError,
    onError = Black,
    errorContainer = DarkError,
    onErrorContainer = Black,
    outline = White.copy(alpha = 0.38f),
    outlineVariant = White.copy(alpha = 0.12f),
    inverseSurface = White,
    inverseOnSurface = Black,
    inversePrimary = White,
    scrim = Black
)

private val LightColorPalette = lightColorScheme(
    primary = White,
    primaryContainer = White,
    onPrimary = Black,
    onPrimaryContainer = Black,
    secondary = Black,
    secondaryContainer = Black,
    onSecondary = White,
    onSecondaryContainer = White,
    tertiary = Black,
    tertiaryContainer = Black,
    onTertiary = White,
    onTertiaryContainer = White,
    primaryFixed = White,
    primaryFixedDim = White,
    onPrimaryFixed = Black,
    onPrimaryFixedVariant = Black,
    secondaryFixed = Black,
    secondaryFixedDim = Black,
    onSecondaryFixed = White,
    onSecondaryFixedVariant = White,
    tertiaryFixed = Black,
    tertiaryFixedDim = Black,
    onTertiaryFixed = White,
    onTertiaryFixedVariant = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = White,
    onSurfaceVariant = Black.copy(alpha = 0.6f),
    surfaceTint = Color.Transparent,
    surfaceDim = White,
    surfaceBright = White,
    surfaceContainerLowest = White,
    surfaceContainerLow = White,
    surfaceContainer = White,
    surfaceContainerHigh = White,
    surfaceContainerHighest = White,
    error = LightError,
    onError = White,
    errorContainer = LightError,
    onErrorContainer = White,
    outline = Black.copy(alpha = 0.38f),
    outlineVariant = Black.copy(alpha = 0.12f),
    inverseSurface = Black,
    inverseOnSurface = White,
    inversePrimary = Black,
    scrim = Black
)

@Composable
fun ChimtubeWorldTheme(darkTheme: Boolean = false, content: @Composable() () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes
    ) {
        // Material 2's default body content used high-emphasis alpha. Explicit Text colors
        // continue to override this; M3 has no LocalContentAlpha.
        CompositionLocalProvider(
            LocalContentColor provides colors.onBackground.copy(alpha = 0.87f),
            content = content
        )
    }
}
