package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Maghrib sky: Sunset with sun setting on horizon.
 */
object MaghribSky {

    val gradientColors = listOf(
        Color(0xFF5D6D7E),  // Grey-blue
        Color(0xFFAF7AC5),  // Soft purple
        Color(0xFFE59866),  // Warm orange
        Color(0xFFF5B041)   // Golden yellow
    )

    val foregroundColor = Color.White

    val hillColor = Color(0xFF2C3E50)

    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

        // Setting sun (partially hidden behind hills)
        val sunCenter = Offset(width * 0.75f, height * 0.7f)
        drawCircle(
            color = Color(0xFFFFCC80).copy(alpha = 0.5f),
            radius = 60f,
            center = sunCenter
        )
        drawCircle(
            color = Color(0xFFFF8A65).copy(alpha = 0.8f),
            radius = 25f,
            center = sunCenter
        )
    }
}

