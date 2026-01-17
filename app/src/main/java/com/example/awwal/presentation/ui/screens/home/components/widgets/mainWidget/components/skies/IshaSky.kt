package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Isha sky: Night sky with moon and stars.
 */
object IshaSky {

    val gradientColors = listOf(
        Color(0xFF1B2631),  // Deep navy
        Color(0xFF2C3E50),  // Dark blue-grey
        Color(0xFF34495E),  // Muted blue
        Color(0xFF5D6D7E)   // Soft grey-blue
    )

    val foregroundColor = Color.White

    val hillColor = Color(0xFF0D1B2A)

    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

        // Crescent moon
        val moonCenter = Offset(width * 0.8f, height * 0.25f)
        val moonRadius = 14f

        drawCircle(
            color = Color(0xFFFFFDE7).copy(alpha = 0.15f),
            radius = moonRadius * 2f,
            center = moonCenter
        )
        drawCircle(
            color = Color(0xFFFFFACD),
            radius = moonRadius,
            center = moonCenter
        )
        drawCircle(
            color = gradientColors[1],
            radius = moonRadius * 0.7f,
            center = Offset(moonCenter.x + 5f, moonCenter.y - 2f)
        )

        // Stars
        drawFaintStars(width, height, alpha = 0.7f)
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

