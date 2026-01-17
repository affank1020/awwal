package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Fajr sky: Dawn colors with fading moon and stars.
 */
object FajrSky {

    val gradientColors = listOf(
        Color(0xFF2C3E50),  // Deep blue-grey
        Color(0xFF5D6D7E),  // Soft grey-blue
        Color(0xFFAEB6BF),  // Light grey
        Color(0xFFE8DACC)   // Warm cream (dawn glow)
    )

    val foregroundColor = Color.White

    val hillColor = Color(0xFF1A252F)

    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

        // Subtle crescent moon (top right, fading)
        val moonCenter = Offset(width * 0.85f, height * 0.25f)
        val moonRadius = 12f

        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = moonRadius,
            center = moonCenter
        )
        drawCircle(
            color = gradientColors[1],
            radius = moonRadius * 0.7f,
            center = Offset(moonCenter.x + 4f, moonCenter.y - 2f)
        )

        // Faint stars
        drawFaintStars(width, height, alpha = 0.3f)
    }

    private fun DrawScope.drawFaintStars(width: Float, height: Float, alpha: Float) {
        val starColor = Color.White.copy(alpha = alpha)
        val positions = listOf(
            Offset(width * 0.1f, height * 0.15f),
            Offset(width * 0.2f, height * 0.35f),
            Offset(width * 0.35f, height * 0.12f),
            Offset(width * 0.5f, height * 0.28f),
            Offset(width * 0.65f, height * 0.18f),
            Offset(width * 0.9f, height * 0.22f)
        )

        positions.forEach { pos ->
            drawCircle(
                color = starColor,
                radius = 1.5f,
                center = pos
            )
        }
    }
}
