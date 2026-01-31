package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.asr

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawCloud
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyView

@Composable
fun AsrSkyView(
    modifier: Modifier = Modifier,
) {
    fun DrawScope.drawSkyElements() {
        val width = size.width
        val height = size.height
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

        drawCloud(Offset(width * 0.25f, height * 0.25f), 0.7f)
    }

    SkyView(
        AsrSky,
        drawSkyElements = { drawSkyElements() },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Asr Sky")
@Composable
private fun AsrSkyPreview() {
    AsrSkyView()
}

