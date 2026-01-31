package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawBirds
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawLandscape

@Composable
fun SkyView(
    prayerSkyConfig: SkyConfig,
    drawSkyElements: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
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
            .background(brush = Brush.verticalGradient(prayerSkyConfig.gradientColors))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSkyElements()
            drawBirds(birdProgress, prayerSkyConfig.foregroundColor.copy(alpha = 0.3f))
            drawLandscape(prayerSkyConfig.hillColor)
        }
    }
}

