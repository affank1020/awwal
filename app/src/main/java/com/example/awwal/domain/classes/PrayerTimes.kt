package com.example.awwal.domain.classes

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Represents the calculated prayer times for a specific date
 */
data class PrayerTimes(
    val date: LocalDate,
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime
) {
    /**
     * Get prayer time by name
     */
    fun getTimeForPrayer(prayerName: String): LocalTime? {
        return when (prayerName.lowercase()) {
            "fajr" -> fajr
            "sunrise" -> sunrise
            "dhuhr" -> dhuhr
            "asr" -> asr
            "maghrib" -> maghrib
            "isha" -> isha
            else -> null
        }
    }

    /**
     * Get the end time for a prayer (which is the start of the next prayer)
     */
    fun getEndTimeForPrayer(prayerName: String): LocalTime? {
        return when (prayerName.lowercase()) {
            "fajr" -> sunrise
            "dhuhr" -> asr
            "asr" -> maghrib
            "maghrib" -> isha
            "isha" -> null // Ends at Fajr next day
            else -> null
        }
    }

    /**
     * Get all prayer names with their times as a map
     */
    fun toMap(): Map<String, LocalTime> {
        return mapOf(
            "Fajr" to fajr,
            "Dhuhr" to dhuhr,
            "Asr" to asr,
            "Maghrib" to maghrib,
            "Isha" to isha
        )
    }

    /**
     * Get formatted times as strings
     */
    fun toFormattedMap(): Map<String, String> {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return mapOf(
            "Fajr" to fajr.format(formatter),
            "Sunrise" to sunrise.format(formatter),
            "Dhuhr" to dhuhr.format(formatter),
            "Asr" to asr.format(formatter),
            "Maghrib" to maghrib.format(formatter),
            "Isha" to isha.format(formatter)
        )
    }

    /**
     * Get sunrise formatted
     */
    fun getSunriseFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return sunrise.format(formatter)
    }

    /**
     * Get sunset (maghrib) formatted
     */
    fun getSunsetFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return maghrib.format(formatter)
    }
}
