package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr

import androidx.compose.ui.graphics.Color
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyConfig

object FajrSky: SkyConfig {
    override val gradientColors = listOf(
        Color(0xFF2C3E50),
        Color(0xFF5D6D7E),
        Color(0xFFAEB6BF),
        Color(0xFFE8DACC)
    )

    override val foregroundColor = Color.White

    override val hillColor = Color(0xFF1A252F)
}
