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
import com.example.awwal.presentation.ui.theme.*

// Dark Mode: Deep backgrounds with lighter accents
private val DarkColorScheme = darkColorScheme(
//    primary = Color(0xFF1F6F5C),        // Emerald green
//    onPrimary = Color(0xFFFFFFFF),
//
//    primaryContainer = Color(0xFFBFE6D8),
//    onPrimaryContainer = Color(0xFF002019),
//
//    secondary = Color(0xFF5A6F63),       // Muted green-gray
//    onSecondary = Color(0xFFFFFFFF),
//
//    secondaryContainer = Color(0xFFDDE5E0),
//    onSecondaryContainer = Color(0xFF171D1A),
//
//    tertiary = Color(0xFF8C6D1F),          // Soft gold
//    onTertiary = Color(0xFFFFFFFF),
//
//    surface = Color(0xFFF6F7F4),          // Warm off-white
//    onSurface = Color(0xFF1B1C1A),
//
//    surfaceVariant = Color(0xFFE1E3DF),    // Card backgrounds
//    onSurfaceVariant = Color(0xFF444844),
//
//    outline = Color(0xFF7A7D78),
//    outlineVariant = Color(0xFFC4C7C2),

    // BLUE

    primary = Color(0xFF4C7AA6)  ,        // Rich desaturated blue
    onPrimary = Color(0xFF0B1A26),

    primaryContainer = Color(0xFF1E3A55),
    onPrimaryContainer = Color(0xFFD6E6F2),

    secondary = Color(0xFF8FA7BF)      ,  // Cool steel blue
    onSecondary = Color(0xFF10212E),

    secondaryContainer = Color(0xFF2A3F52),
    onSecondaryContainer = Color(0xFFDCE6EF),

    tertiary = Color(0xFFC9A24D)     ,     // Muted gold
    onTertiary = Color(0xFF2A1E05),

    surface = Color(0xFF0E1620)      ,     // Deep navy background
    onSurface = Color(0xFFE6EDF3),

    surfaceVariant = Color(0xFF182331)  ,  // Card surfaces
    onSurfaceVariant = Color(0xFFCBD5DF),

    outline = Color(0xFF3B4A5C),
    outlineVariant = Color(0xFF223040),

    background = Color(0xFF080E15),    // Very deep navy-black
    onBackground = Color(0xFFE6EDF3),   // Soft cool white



            //GREEN
//    primary = Color(0xFF6FAF8E)  ,        // Muted jade green
//    onPrimary = Color(0xFF10241B),
//
//    primaryContainer = Color(0xFF2F5A46),
//    onPrimaryContainer = Color(0xFFD8EFE4),
//
//    secondary = Color(0xFF9B8365)    ,    // Warm brown
//    onSecondary = Color(0xFF2A1F14),
//
//    secondaryContainer = Color(0xFF3F3326),
//    onSecondaryContainer = Color(0xFFE9DED1),
//
//    tertiary = Color(0xFFC6A25A)    ,      // Aged gold
//    onTertiary = Color(0xFF2E2108),
//
//    surface = Color(0xFF121815)  ,         // Dark moss green
//    onSurface = Color(0xFFE7ECE8),
//
//    surfaceVariant = Color(0xFF1C2621)  ,  // Elevated card green
//    onSurfaceVariant = Color(0xFFC8D2CB),
//
//    outline = Color(0xFF3E4A43),
//    outlineVariant = Color(0xFF26332C),
//
//    background = Color(0xFF0C1210)  ,   // Deep moss green-black
//    onBackground = Color(0xFFE7ECE8)   // Soft neutral light


            //indigo

//    primary = Color(0xFF7B8CDE)  ,        // Soft indigo
//    onPrimary = Color(0xFF161A33),
//
//    primaryContainer = Color(0xFF2E335E),
//    onPrimaryContainer = Color(0xFFE0E4FF),
//
//    secondary = Color(0xFF5FA3A2)     ,   // Dusty teal
//    onSecondary = Color(0xFF0F2A2A),
//
//    secondaryContainer = Color(0xFF254443),
//    onSecondaryContainer = Color(0xFFD4ECEB),
//
//    tertiary = Color(0xFFB79A5A)   ,       // Brass
//    onTertiary = Color(0xFF2A2009),
//
//    surface = Color(0xFF12131A)    ,       // Indigo-black
//    onSurface = Color(0xFFE9E9EE),
//
//    surfaceVariant = Color(0xFF1C1E29) ,   // Indigo cards
//    onSurfaceVariant = Color(0xFFD0D2E0),
//
//    outline = Color(0xFF3E4052),
//    outlineVariant = Color(0xFF262838),
//
//    background = Color(0xFF0B0C13) ,    // Indigo-black
//    onBackground = Color(0xFFE9E9EE) ,  // Cool soft white


    //MODERN DARK

//    primary = Color(0xFF8FA2B8) ,         // Soft slate blue
//    onPrimary = Color(0xFF0F1A26),
//
//    primaryContainer = Color(0xFF2B3543),
//    onPrimaryContainer = Color(0xFFDCE3EC),
//
//    secondary = Color(0xFF9AA0A6)  ,      // Neutral grey
//    onSecondary = Color(0xFF1B1F23),
//
//    secondaryContainer = Color(0xFF3A3F45),
//    onSecondaryContainer = Color(0xFFDADDE1),
//
//    tertiary = Color(0xFFB0B4BA),
//    onTertiary = Color(0xFF1E2125),
//
//    surface = Color(0xFF121316)    ,       // Dark background
//    onSurface = Color(0xFFE6E6E6),
//
//    surfaceVariant = Color(0xFF1E2024),
//    onSurfaceVariant = Color(0xFFC7C7C7),
//
//    outline = Color(0xFF8A8D91),
//    outlineVariant = Color(0xFF3A3D42),
//
//    background = Color(0xFF0E0F12) ,   // Neutral dark grey
//    onBackground = Color(0xFFE6E6E6)   // Standard light text


)

private val LightColorScheme = lightColorScheme(
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