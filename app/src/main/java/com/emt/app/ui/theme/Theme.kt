package com.emt.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary            = Teal700,
    onPrimary          = androidx.compose.ui.graphics.Color.White,
    primaryContainer   = TealLight,
    secondary          = Amber600,
    onSecondary        = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = AmberLight,
    error              = Red500,
    errorContainer     = RedLight,
    background         = Grey50,
    surface            = androidx.compose.ui.graphics.Color.White,
    onBackground       = Grey900,
    onSurface          = Grey900,
    surfaceVariant     = Grey100,
    outline            = Grey200
)

private val DarkColors = darkColorScheme(
    primary            = TealLight,
    onPrimary          = Teal900,
    primaryContainer   = Teal700,
    secondary          = AmberLight,
    onSecondary        = Amber600,
    background         = DarkBg,
    surface            = DarkSurface,
    onBackground       = androidx.compose.ui.graphics.Color.White,
    onSurface          = androidx.compose.ui.graphics.Color.White,
    surfaceVariant     = DarkSurface2
)

@Composable
fun EMTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = EMTTypography,
        content     = content
    )
}
