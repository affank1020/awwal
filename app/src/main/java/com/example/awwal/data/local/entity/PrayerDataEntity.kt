package com.example.awwal.data.local.entity

import androidx.room.Entity
import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalDate

@Entity(
    tableName = "prayer_data",
    primaryKeys = ["prayerName", "date"]
)
data class PrayerDataEntity(
    val prayerName: String,
    val date: LocalDate,
    val status: PrayerStatus
    // Add other properties as needed (e.g., time prayed, notes, etc.)
)

