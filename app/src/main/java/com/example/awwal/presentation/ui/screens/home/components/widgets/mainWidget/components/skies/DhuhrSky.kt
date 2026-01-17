package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Dhuhr sky: Bright midday with sun at zenith and clouds.
 */
object DhuhrSky {

    val gradientColors = listOf(
        Color(0xFF5DADE2),  // Soft sky blue
        Color(0xFF85C1E9),  // Light blue
        Color(0xFFAED6F1),  // Pale blue
        Color(0xFFF8F9F9)   // Almost white
    )

    val foregroundColor = Color(0xFF2C3E50)  // Dark for light sky

    val hillColor = Color(0xFF58A55C)  // Green hills

    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

        // Soft sun glow (top area)
        val sunCenter = Offset(width * 0.75f, height * 0.15f)
        drawCircle(
            color = Color(0xFFFFF9C4).copy(alpha = 0.4f),
            radius = 40f,
            center = sunCenter
        )
        drawCircle(
            color = Color(0xFFFFEB3B).copy(alpha = 0.6f),
            radius = 18f,
            center = sunCenter
        )

        // Fluffy clouds
        drawCloud(Offset(width * 0.2f, height * 0.3f), 0.8f)
        drawCloud(Offset(width * 0.5f, height * 0.2f), 1f)
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

