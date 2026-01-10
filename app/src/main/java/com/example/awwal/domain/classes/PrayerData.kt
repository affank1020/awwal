package com.example.awwal.domain.classes

import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalDate
import java.time.LocalTime

data class PrayerData(
    val prayerName: String,
    val date: LocalDate,
    val prayerStatus: PrayerStatus,
    val timePrayed: LocalTime? = null,
    val prayerWindowPercentage: Float? = null // 0.0 = start of window, 1.0 = end of window
)
