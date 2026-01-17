package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Common drawing utilities shared across all prayer skies.
 */
object SkyDrawingUtils {

    /**
     * Draws rolling hills/landscape silhouette at the bottom.
     */
    fun DrawScope.drawLandscape(hillColor: Color) {
        val width = size.width
        val height = size.height

        // Back hills (lighter, further away)
        val backHillPath = Path().apply {
            moveTo(0f, height)

            val hillHeight1 = height * 0.7f
            val hillHeight2 = height * 0.75f
            val hillHeight3 = height * 0.72f

            cubicTo(
                width * 0.15f, hillHeight1,
                width * 0.25f, hillHeight2,
                width * 0.4f, hillHeight1
            )
            cubicTo(
                width * 0.55f, hillHeight3,
                width * 0.7f, hillHeight2,
                width * 0.85f, hillHeight1
            )
            cubicTo(
                width * 0.95f, hillHeight3,
                width, hillHeight2,
                width, height
            )
            close()
        }

        drawPath(
            path = backHillPath,
            color = hillColor.copy(alpha = 0.5f)
        )

        // Front hills (darker, closer)
        val frontHillPath = Path().apply {
            moveTo(0f, height)

            val hillHeight1 = height * 0.8f
            val hillHeight2 = height * 0.85f
            val hillHeight3 = height * 0.82f

            cubicTo(
                width * 0.1f, hillHeight2,
                width * 0.2f, hillHeight1,
                width * 0.35f, hillHeight3
            )
            cubicTo(
                width * 0.5f, hillHeight1,
                width * 0.65f, hillHeight2,
                width * 0.8f, hillHeight1
            )
            cubicTo(
                width * 0.9f, hillHeight3,
                width * 0.95f, hillHeight2,
                width, height
            )
            close()
        }

        drawPath(
            path = frontHillPath,
            color = hillColor
        )
    }

    /**
     * Draws animated birds flying across the sky.
     * @param progress Animation progress from 0f to 1f (birds fly left to right)
     * @param birdColor Color of the birds
     */
    fun DrawScope.drawBirds(progress: Float, birdColor: Color = Color.Black.copy(alpha = 0.6f)) {
        val width = size.width
        val height = size.height

        // Bird flock positions (relative, will be offset by progress)
        val birdOffsets = listOf(
            Offset(0f, 0f),
            Offset(25f, -8f),
            Offset(15f, 12f),
            Offset(45f, 5f),
            Offset(35f, -15f)
        )

        // Calculate base position based on progress (fly from left to right)
        val baseX = -60f + (width + 120f) * progress
        val baseY = height * 0.25f

        birdOffsets.forEach { offset ->
            drawBird(
                center = Offset(baseX + offset.x, baseY + offset.y),
                color = birdColor,
                size = 8f
            )
        }
    }

    /**
     * Draws a simple "V" shaped bird.
     */
    private fun DrawScope.drawBird(center: Offset, color: Color, size: Float) {
        val path = Path().apply {
            // Left wing
            moveTo(center.x - size, center.y + size * 0.3f)
            quadraticTo(
                center.x - size * 0.3f, center.y - size * 0.2f,
                center.x, center.y
            )
            // Right wing
            quadraticTo(
                center.x + size * 0.3f, center.y - size * 0.2f,
                center.x + size, center.y + size * 0.3f
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.5f)
        )
    }
}

