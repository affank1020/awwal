package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

object SkyDrawingUtils {
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

    fun DrawScope.drawFaintStars(width: Float, height: Float, alpha: Float) {
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

    fun DrawScope.drawCloud(center: Offset, scale: Float) {
        val cloudColor = Color.White.copy(alpha = 0.7f)
        val baseRadius = 12f * scale

        drawCircle(cloudColor, baseRadius, Offset(center.x - baseRadius, center.y))
        drawCircle(cloudColor, baseRadius * 1.3f, center)
        drawCircle(cloudColor, baseRadius * 1.1f, Offset(center.x + baseRadius, center.y))
        drawCircle(cloudColor, baseRadius * 0.9f, Offset(center.x + baseRadius * 1.8f, center.y + 2f))
        drawCircle(cloudColor, baseRadius * 0.8f, Offset(center.x - baseRadius * 1.5f, center.y + 2f))
    }

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

