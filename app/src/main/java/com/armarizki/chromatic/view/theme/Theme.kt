package com.armarizki.chromatic.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.rohankhayech.android.util.ui.preview.m3.ColorSwatch
import com.rohankhayech.android.util.ui.theme.m3.AdaptableMaterialTheme
import com.rohankhayech.android.util.ui.theme.m3.dynamicTrueDarkColorScheme
import com.rohankhayech.android.util.ui.theme.m3.trueDark

/**
 * Theme for the app.
 *
 * @param darkTheme Whether to use the dark variant of this theme.
 * @param fullBlack Whether to use full black colors when [darkTheme] is enabled.
 * @param dynamicColor Whether to use dynamic color.
 * @param content Content to display with this theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fullBlack: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AdaptableMaterialTheme(
        lightColorScheme = LightColorScheme,
        darkColorScheme = DarkColorScheme,
        trueDarkColorScheme = TrueDarkColorScheme,
        dynamicTrueDarkColorScheme = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicTrueDarkColorScheme(LocalContext.current).copy(surface = Color.Black) else DarkColorScheme.trueDark().copy(surface = Color.Black) },
        darkTheme = darkTheme,
        trueDark = fullBlack,
        dynamicColor = dynamicColor,
        content = content
    )
}

@Composable
fun PreviewWrapper(
    fullBlack: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(fullBlack = fullBlack, dynamicColor = dynamicColor) {
        Surface(content = content)
    }
}

@PreviewLightDark
@Composable
fun AppThemePreview() {
    PreviewWrapper { ColorSwatch() }
}