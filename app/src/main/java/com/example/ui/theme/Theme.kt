package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CreativeColorScheme = lightColorScheme(
    primary = HeadingBlack,
    onPrimary = BackgroundIvory,
    secondary = AccentLime,
    onSecondary = HeadingBlack,
    tertiary = CardCharcoal,
    background = BackgroundIvory,
    surface = SolidWhite,
    onBackground = HeadingBlack,
    onSurface = HeadingBlack,
    surfaceVariant = SoftGray,
    onSurfaceVariant = HeadingBlack
)

private val CreativeDarkColorScheme = darkColorScheme(
    primary = AccentLime,
    onPrimary = DarkPremium,
    secondary = AccentLime,
    onSecondary = DarkPremium,
    tertiary = CardCharcoal,
    background = DarkPremium,
    surface = CardCharcoal,
    onBackground = BackgroundIvory,
    onSurface = BackgroundIvory,
    surfaceVariant = CardCharcoal,
    onSurfaceVariant = BackgroundIvory
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) CreativeDarkColorScheme else CreativeColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
