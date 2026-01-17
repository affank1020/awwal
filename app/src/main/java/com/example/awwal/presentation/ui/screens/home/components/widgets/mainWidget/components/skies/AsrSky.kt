package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Asr sky: Warm afternoon with sun lower in sky.
 */
object AsrSky {

    val gradientColors = listOf(
        Color(0xFF85C1E9),  // Light blue
        Color(0xFFF9E79F),  // Soft yellow
        Color(0xFFFAD7A0),  // Warm peach
        Color(0xFFF5CBA7)   // Soft orange
    )

    val foregroundColor = Color(0xFF2C3E50)  // Dark for warm sky

    val hillColor = Color(0xFF6B8E4E)  // Olive green

    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

        // Warm sun (lower right)
        val sunCenter = Offset(width * 0.8f, height * 0.35f)
        drawCircle(
            color = Color(0xFFFFF176).copy(alpha = 0.3f),
            radius = 50f,
            center = sunCenter
        )
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.7f),
            radius = 20f,
            center = sunCenter
        )

        // Light cloud
        drawCloud(Offset(width * 0.25f, height * 0.25f), 0.7f)
    }

    private fun DrawScope.drawCloud(center: Offset, scale: Float) {
        val cloudColor = Color.White.copy(alpha = 0.7f)
        val baseRadius = 12f * scale

        drawCircle(cloudColor, baseRadius, Offset(center.x - baseRadius, center.y))
        drawCircle(cloudColor, baseRadius * 1.3f, center)
        drawCircle(cloudColor, baseRadius * 1.1f, Offset(center.x + baseRadius, center.y))
        drawCircle(cloudColor, baseRadius * 0.9f, Offset(center.x + baseRadius * 1.8f, center.y + 2f))
        drawCircle(cloudColor, baseRadius * 0.8f, Offset(center.x - baseRadius * 1.5f, center.y + 2f))
    }
}

