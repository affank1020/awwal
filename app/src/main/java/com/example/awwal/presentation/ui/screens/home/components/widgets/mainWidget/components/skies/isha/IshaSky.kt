package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha

import androidx.compose.ui.graphics.Color
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyConfig

object IshaSky: SkyConfig {
    override val gradientColors = listOf(
        Color(0xFF1B2631),
        Color(0xFF2C3E50),
        Color(0xFF34495E),
        Color(0xFF5D6D7E)
    )

    override val foregroundColor = Color.White

    override val hillColor = Color(0xFF0D1B2A)
}

