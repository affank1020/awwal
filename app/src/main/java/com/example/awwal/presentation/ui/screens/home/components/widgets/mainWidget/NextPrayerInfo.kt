package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun NextPrayerInfo(
    nextEventLabel: String,
    nextEventTime: LocalTime?,
    now: LocalTime,
    currentPrayerName: String,
    foregroundColor: Color
) {
    val timeUntilNext = nextEventTime?.let {
        val duration = if (currentPrayerName == "Isha" && it < now) {
            val secondsUntilMidnight = Duration.between(now, LocalTime.MAX).seconds + 1
            val secondsAfterMidnight = Duration.between(LocalTime.MIDNIGHT, it).seconds
            Duration.ofSeconds(secondsUntilMidnight + secondsAfterMidnight)
        } else {
            Duration.between(now, it)
        }
        if (!duration.isNegative) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d",
                duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
        } else "--"
    } ?: "--"
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "$nextEventLabel at ${nextEventTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "--"}",
            style = MaterialTheme.typography.bodyLarge,
            color = foregroundColor.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "In $timeUntilNext",
            style = MaterialTheme.typography.bodyMedium,
            color = foregroundColor.copy(alpha = 0.7f)
        )
    }
}
