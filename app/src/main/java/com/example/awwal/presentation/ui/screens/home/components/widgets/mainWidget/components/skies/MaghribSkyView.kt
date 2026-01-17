package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.MaghribSky.drawSkyElements
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawBirds
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawLandscape

/**
 * Maghrib sky composable with animation support.
 */
@Composable
fun MaghribSkyView(
    modifier: Modifier = Modifier,
    showBirds: Boolean = true
) {
    // Bird animation - flies across every 8 seconds
    val infiniteTransition = rememberInfiniteTransition(label = "birds")
    val birdProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "birdFlight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(brush = Brush.verticalGradient(MaghribSky.gradientColors))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSkyElements()
            if (showBirds) {
                drawBirds(birdProgress, MaghribSky.foregroundColor.copy(alpha = 0.4f))
            }
            drawLandscape(MaghribSky.hillColor)
        }
    }
}

@Preview(showBackground = true, name = "Maghrib Sky")
@Composable
private fun MaghribSkyPreview() {
    MaghribSkyView(showBirds = false)
}

