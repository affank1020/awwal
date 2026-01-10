package com.example.awwal.data.local

import androidx.room.TypeConverter
import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalDate
import java.time.LocalTime

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
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
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
