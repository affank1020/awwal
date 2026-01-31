package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.dhuhr

import androidx.compose.ui.graphics.Color
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyConfig

object DhuhrSky: SkyConfig {
    override val gradientColors = listOf(
        Color(0xFF5DADE2),
        Color(0xFF85C1E9),
        Color(0xFFAED6F1),
        Color(0xFFF8F9F9)
    )

    override val foregroundColor = Color(0xFF2C3E50)

    override val hillColor = Color(0xFF58A55C)
}

