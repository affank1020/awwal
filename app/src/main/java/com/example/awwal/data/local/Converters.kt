package com.example.awwal.data.local

import androidx.room.TypeConverter
import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromPrayerStatus(status: PrayerStatus): String {
        return status.name
    }

    @TypeConverter
    fun toPrayerStatus(statusString: String): PrayerStatus {
        return PrayerStatus.valueOf(statusString)
    }
}

