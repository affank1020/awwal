package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.dhuhr

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawCloud
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyView

@Composable
fun DhuhrSkyView(
    modifier: Modifier = Modifier,
) {
    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height
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

        drawCloud(Offset(width * 0.2f, height * 0.3f), 0.8f)
        drawCloud(Offset(width * 0.5f, height * 0.2f), 1f)
    }

    SkyView(
        DhuhrSky,
        drawSkyElements = { drawSkyElements() },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Dhuhr Sky")
@Composable
private fun DhuhrSkyPreview() {
    DhuhrSkyView()
}

