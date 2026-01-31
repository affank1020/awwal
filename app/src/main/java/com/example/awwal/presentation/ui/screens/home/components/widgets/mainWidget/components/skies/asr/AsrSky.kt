package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.asr

import androidx.compose.ui.graphics.Color
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyConfig

object AsrSky: SkyConfig {
    override val gradientColors = listOf(
        Color(0xFF85C1E9),
        Color(0xFFF9E79F),
        Color(0xFFFAD7A0),
        Color(0xFFF5CBA7)
    )

    override val foregroundColor = Color(0xFF2C3E50)

    override val hillColor = Color(0xFF6B8E4E)
}

