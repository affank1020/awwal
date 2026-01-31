package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawFaintStars
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr.FajrSky.gradientColors

/**
 * Fajr sky composable with animation support.
 */
@Composable
fun FajrSkyView(
    modifier: Modifier = Modifier,
) {
    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

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

        drawFaintStars(width, height, alpha = 0.3f)
    }

    SkyView(
        FajrSky,
        drawSkyElements = { drawSkyElements() },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Fajr Sky")
@Composable
private fun FajrSkyPreview() {
    FajrSkyView()
}
