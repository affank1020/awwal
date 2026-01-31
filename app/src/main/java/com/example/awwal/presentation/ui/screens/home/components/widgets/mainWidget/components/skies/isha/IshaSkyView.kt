package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyDrawingUtils.drawFaintStars
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.SkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha.IshaSky.gradientColors

@Composable
fun IshaSkyView(
    modifier: Modifier = Modifier,
) {
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

        drawFaintStars(width, height, alpha = 0.7f)
    }

    SkyView(
        IshaSky,
        drawSkyElements = { drawSkyElements() },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Isha Sky")
@Composable
private fun IshaSkyPreview() {
    IshaSkyView()
}

