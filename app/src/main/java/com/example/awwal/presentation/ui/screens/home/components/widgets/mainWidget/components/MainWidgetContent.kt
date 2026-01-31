package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.MainWidgetUiState
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.asr.AsrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.dhuhr.DhuhrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.fajr.FajrSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.isha.IshaSky
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.skies.maghrib.MaghribSky
import java.time.format.DateTimeFormatter

@Composable
fun MainWidgetContent(
    state: MainWidgetUiState,
    onMarkPrayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        PrayerBanner(
            currentPrayerName = state.currentPrayerName,
            state = state,
            formatter = formatter,
        )

        val bgColour = when (state.currentPrayerName.lowercase()) {
            "fajr" -> FajrSky.hillColor
            "dhuhr" -> DhuhrSky.hillColor
            "asr" -> AsrSky.hillColor
            "maghrib" -> MaghribSky.hillColor
            "isha" -> IshaSky.hillColor
            else -> MaterialTheme.colorScheme.surface
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColour)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prayer status section
            PrayerStatusSection(
                state = state,
                onMarkPrayerClick = onMarkPrayerClick
            )

            // Next prayer info
            NextPrayerInfo(
                nextEventLabel = state.nextEventLabel,
                nextEventTime = state.nextEventTime,
                now = state.currentTime,
                currentPrayerName = state.currentPrayerName,
                foregroundColor = MaterialTheme.colorScheme.onSurface
            )

            // Sunrise/Sunset row
            SunTimesRow(
                sunriseTime = state.sunriseTime,
                sunsetTime = state.sunsetTime,
                formatter = formatter
            )
        }
    }
}