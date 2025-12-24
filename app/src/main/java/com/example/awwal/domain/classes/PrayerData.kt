package com.example.awwal.domain.classes

import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalDate

data class PrayerData(
    val prayerName: String,
    val date: LocalDate,
    val prayerStatus: PrayerStatus
)
