package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Prayer banner that displays the current date/time with a prayer-specific
 * animated sky background.
 */
@Composable
fun PrayerBanner(
    currentPrayerName: String,
    currentDate: LocalDate,
    currentTime: LocalTime,
    modifier: Modifier = Modifier,
    showBirds: Boolean = true
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")

    val foregroundColor = getForegroundColor(currentPrayerName)

    Box(modifier = modifier.fillMaxWidth()) {
        // Render the appropriate sky based on prayer time
        when (currentPrayerName.lowercase()) {
            "fajr" -> FajrSkyView(showBirds = showBirds)
            "dhuhr" -> DhuhrSkyView(showBirds = showBirds)
            "asr" -> AsrSkyView(showBirds = showBirds)
            "maghrib" -> MaghribSkyView(showBirds = showBirds)
            "isha" -> IshaSkyView(showBirds = false) // No birds at night
            else -> DhuhrSkyView(showBirds = showBirds)
        }

        // Date and Time overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Text(
                text = currentDate.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                color = foregroundColor,
            )
            Text(
                text = currentTime.format(timeFormatter),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = foregroundColor
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