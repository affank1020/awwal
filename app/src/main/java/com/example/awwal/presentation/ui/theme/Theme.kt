package com.example.awwal.presentation.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.awwal.domain.classes.enums.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = MutedTeal,
    secondary = Teal,
    tertiary = AshGrey,
    background = InkBlack,
    surface = InkBlack,
    onPrimary = InkBlack,
    onSecondary = VanillaCream,
    onTertiary = InkBlack,
    onBackground = VanillaCream,
    onSurface = VanillaCream
)

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    secondary = MutedTeal,
    tertiary = AshGrey,
    background = VanillaCream,
    surface = VanillaCream,
    onPrimary = VanillaCream,
    onSecondary = InkBlack,
    onTertiary = InkBlack,
    onBackground = InkBlack,
    onSurface = InkBlack
)

@Composable
fun AwwalTheme(
    theme: ThemeMode = ThemeMode.AUTO,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (theme === ThemeMode.DARK) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        theme === ThemeMode.DARK -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}