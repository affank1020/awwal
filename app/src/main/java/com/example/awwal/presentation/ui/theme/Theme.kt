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

// Dark Mode: Deep backgrounds with lighter accents
private val DarkColorScheme = darkColorScheme(
    primary = Teal,                    // Bright teal for primary actions (stands out on dark)
    onPrimary = InkBlack,              // Dark text on bright teal buttons
    primaryContainer = MutedTeal,      // Muted teal for containers
    onPrimaryContainer = VanillaCream, // Cream text on muted containers

    secondary = MutedTeal,             // Muted teal for secondary elements
    onSecondary = VanillaCream,        // Cream text on secondary
    secondaryContainer = AshGrey,      // Grey containers for secondary
    onSecondaryContainer = VanillaCream,

    tertiary = VanillaCream,           // Cream as tertiary accent
    onTertiary = InkBlack,

    background = InkBlack,             // Deep black background
    onBackground = VanillaCream,       // Cream text on dark background

    surface = DarkInkBlack,       // Slightly lighter than background for cards
    onSurface = VanillaCream,          // Cream text on surfaces
    surfaceVariant = Color(0xFF2C2C2E),// Even lighter for elevated surfaces
    onSurfaceVariant = Color(0xFFE8DCC4), // Slightly dimmed cream

    outline = AshGrey,                 // Grey for borders/dividers
    outlineVariant = Color(0xFF3A3A3C) // Darker grey for subtle borders
)

// Light Mode: Light backgrounds with darker accents
private val LightColorScheme = lightColorScheme(
    primary = MutedTeal,               // Muted teal for primary (readable on light)
    onPrimary = VanillaCream,          // Light text on muted teal buttons
    primaryContainer = Color(0xFFE0F2F1), // Very light teal container
    onPrimaryContainer = Color(0xFF004D40), // Dark teal text

    secondary = Teal,                  // Bright teal for secondary
    onSecondary = InkBlack,            // Dark text on bright teal
    secondaryContainer = Color(0xFFB2DFDB), // Light teal container
    onSecondaryContainer = InkBlack,

    tertiary = Color(0xFF6D4C41),      // Warm brown tertiary (complements cream)
    onTertiary = VanillaCream,

    background = VanillaCream,         // Cream background
    onBackground = InkBlack,           // Dark text on cream

    surface = Color(0xFFFFFBF5),       // Slightly warmer white for cards
    onSurface = InkBlack,              // Dark text on light surfaces
    surfaceVariant = Color(0xFFF5F0E8),// Warmer cream variant
    onSurfaceVariant = Color(0xFF2C2C2C), // Dark grey text

    outline = AshGrey,                 // Grey for borders/dividers
    outlineVariant = Color(0xFFD6D6D6) // Light grey for subtle borders
)

@Composable
fun AwwalTheme(
    theme: ThemeMode = ThemeMode.AUTO,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Determine if we should use dark theme
    val isDarkTheme = when (theme) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val colorScheme = when {
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}