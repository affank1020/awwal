package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget

import androidx.compose.ui.graphics.Color
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.getAccentColor
import java.time.LocalDate
import java.time.LocalTime

data class MainWidgetUiState(
    val currentPrayerName: String,
    val currentDate: LocalDate,
    val currentTime: LocalTime,
    val hasPrayed: Boolean,
    val currentStatus: PrayerStatus,
    val timePrayed: LocalTime?,
    val nextEventLabel: String,
    val nextEventTime: LocalTime?,
    val sunriseTime: LocalTime?,
    val sunsetTime: LocalTime?,
    val currentPrayerStartTime: LocalTime?,
    val currentPrayerEndTime: LocalTime?
) {
    val accentColor: Color
        get() = getAccentColor(currentPrayerName)

    companion object {
        /**
         * Creates a preview-friendly state for a specific prayer time.
         */
        fun forPrayer(
            prayerName: String,
            time: LocalTime = LocalTime.of(12, 0),
            hasPrayed: Boolean = false,
            status: PrayerStatus = PrayerStatus.EMPTY
        ): MainWidgetUiState {
            val nextPrayer = when (prayerName) {
                "Fajr" -> "Dhuhr" to LocalTime.of(12, 15)
                "Dhuhr" -> "Asr" to LocalTime.of(15, 45)
                "Asr" -> "Maghrib" to LocalTime.of(18, 30)
                "Maghrib" -> "Isha" to LocalTime.of(20, 0)
                "Isha" -> "Fajr" to LocalTime.of(5, 30)
                else -> "Dhuhr" to LocalTime.of(12, 15)
            }

            return MainWidgetUiState(
                currentPrayerName = prayerName,
                currentDate = LocalDate.now(),
                currentTime = time,
                hasPrayed = hasPrayed,
                currentStatus = status,
                timePrayed = if (hasPrayed) time.minusMinutes(10) else null,
                nextEventLabel = nextPrayer.first,
                nextEventTime = nextPrayer.second,
                sunriseTime = LocalTime.of(6, 45),
                sunsetTime = LocalTime.of(18, 30),
                currentPrayerStartTime = time.minusMinutes(30),
                currentPrayerEndTime = time.plusHours(2)
            )
        }
    }
}