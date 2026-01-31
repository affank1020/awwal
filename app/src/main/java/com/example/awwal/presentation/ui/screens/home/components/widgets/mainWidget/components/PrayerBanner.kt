package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.MainWidgetUiState
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.asr.AsrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.asr.AsrSkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.dhuhr.DhuhrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.dhuhr.DhuhrSkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr.FajrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr.FajrSkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha.IshaSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha.IshaSkyView
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.maghrib.MaghribSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.maghrib.MaghribSkyView
import java.time.format.DateTimeFormatter

/**
 * Prayer banner that displays the current date/time with a prayer-specific
 * animated sky background.
 */
@Composable
fun PrayerBanner(
    currentPrayerName: String,
    state: MainWidgetUiState,
    formatter: DateTimeFormatter,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Render the appropriate sky based on prayer time
        when (currentPrayerName.lowercase()) {
            "fajr" -> FajrSkyView()
            "dhuhr" -> DhuhrSkyView()
            "asr" -> AsrSkyView()
            "maghrib" -> MaghribSkyView()
            "isha" -> IshaSkyView()
            else -> DhuhrSkyView()
        }

        if (state.hasPrayed) {
            val prayedText = buildAnnotatedString {
                append("Prayed ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(state.currentPrayerName)
                }
                if (state.timePrayed != null && state.currentStatus == PrayerStatus.PRAYED) {
                    append(" at ${state.timePrayed.format(formatter)}")
                } else {
                    when (state.currentStatus) {
                        PrayerStatus.JAMAAH -> append(" in Jamaah")
                        PrayerStatus.LATE -> append(" late")
                        else -> {}
                    }
                }
            }

            Text(
                text = prayedText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        } else {
            val promptText = buildAnnotatedString {
                append("Have you prayed ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(state.currentPrayerName)
                }
                append("?")
            }
            Text(
                text = promptText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Foreground color for text readability.
 */
private fun getForegroundColor(prayerName: String): Color {
    return when (prayerName.lowercase()) {
        "fajr" -> FajrSky.foregroundColor
        "dhuhr" -> DhuhrSky.foregroundColor
        "asr" -> AsrSky.foregroundColor
        "maghrib" -> MaghribSky.foregroundColor
        "isha" -> IshaSky.foregroundColor
        else -> Color.White
    }
}