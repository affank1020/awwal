package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.maghrib

import androidx.compose.ui.graphics.Color
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyConfig

object MaghribSky: SkyConfig {

    override val gradientColors = listOf(
        Color(0xFF5D6D7E),
        Color(0xFFAF7AC5),
        Color(0xFFE59866),
        Color(0xFFF5B041)
    )

    override val foregroundColor = Color.White

    override val hillColor = Color(0xFF2C3E50)
}

