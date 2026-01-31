package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.maghrib

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyView

@Composable
fun MaghribSkyView(
    modifier: Modifier = Modifier,
) {
    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height

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

    SkyView(
        MaghribSky,
        drawSkyElements = { drawSkyElements() },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Maghrib Sky")
@Composable
private fun MaghribSkyPreview() {
    MaghribSkyView()
}

