package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.MainWidgetUiState
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
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
    ) {
        // Top Section: Prayer Banner
        PrayerBanner(
            currentPrayerName = state.currentPrayerName,
            currentDate = state.currentDate,
            currentTime = state.currentTime
        )

        // Bottom Section: Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prayer status section
            PrayerStatusSection(
                state = state,
                formatter = formatter,
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