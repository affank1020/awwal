package com.example.awwal.presentation.ui.common.contexts

import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun getPrayerContext(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    now: LocalTime,
    formatter: DateTimeFormatter
): PrayerContext {
    val times = prayerNames.mapNotNull { name ->
        prayerTimes[name]?.let { timeStr ->
            try { name to LocalTime.parse(timeStr, formatter) } catch (_: Exception) { null }
        }
    }
    val fajrTime = times.find { it.first == "Fajr" }?.second
    val sunriseTime = prayerTimes["Sunrise"]?.let { try { LocalTime.parse(it, formatter) } catch (_: Exception) { null } }
    val sunsetTime = prayerTimes["Maghrib"]?.let { try { LocalTime.parse(it, formatter) } catch (_: Exception) { null } }
    val ishaTime = times.find { it.first == "Isha" }?.second
    val isBeforeFajr = fajrTime != null && now < fajrTime
    val isAfterIsha = ishaTime != null && now >= ishaTime
    val currentPrayerIndex: Int
    val currentPrayerName: String
    val currentPrayerStartTime: LocalTime?
    if (isBeforeFajr && !isAfterIsha) {
        currentPrayerName = "Isha"
        currentPrayerStartTime = ishaTime
        currentPrayerIndex = times.indexOfFirst { it.first == "Isha" }
    } else {
        currentPrayerIndex = times.indexOfLast { (_, time) -> now >= time }
        currentPrayerName = times.getOrNull(currentPrayerIndex)?.first ?: "Isha"
        currentPrayerStartTime = times.getOrNull(currentPrayerIndex)?.second
    }
    val currentPrayerData = prayerStatusMap[currentPrayerName]
    val currentStatus = currentPrayerData?.prayerStatus ?: PrayerStatus.EMPTY
    val timePrayed = currentPrayerData?.timePrayed
    val hasPrayed = currentStatus in listOf(PrayerStatus.PRAYED, PrayerStatus.JAMAAH, PrayerStatus.LATE)
    val nextEventTime: LocalTime?
    val nextEventLabel: String
    when {
        currentPrayerName == "Fajr" && !hasPrayed -> {
            nextEventTime = sunriseTime
            nextEventLabel = "Sunrise"
        }
        currentPrayerName == "Fajr" && hasPrayed -> {
            nextEventTime = times.find { it.first == "Dhuhr" }?.second
            nextEventLabel = "Dhuhr"
        }
        currentPrayerName == "Isha" -> {
            nextEventTime = fajrTime
            nextEventLabel = "Fajr"
        }
        else -> {
            val nextPrayerIndex = (currentPrayerIndex + 1).coerceAtMost(times.size - 1)
            val nextPrayer = times.getOrNull(nextPrayerIndex)
            nextEventTime = nextPrayer?.second
            nextEventLabel = nextPrayer?.first ?: "Fajr"
        }
    }
    val currentPrayerEndTime: LocalTime? = when (currentPrayerName) {
        "Fajr" -> sunriseTime
        "Isha" -> fajrTime
        else -> {
            val nextIdx = times.indexOfFirst { it.first == currentPrayerName } + 1
            times.getOrNull(nextIdx)?.second
        }
    }
    return PrayerContext(
        currentPrayerName, currentPrayerStartTime, currentPrayerIndex, currentStatus, timePrayed, hasPrayed,
        nextEventTime, nextEventLabel, fajrTime, sunriseTime, sunsetTime, ishaTime, currentPrayerEndTime
    )
}

data class PrayerContext(
    val currentPrayerName: String,
    val currentPrayerStartTime: LocalTime?,
    val currentPrayerIndex: Int,
    val currentStatus: PrayerStatus,
    val timePrayed: LocalTime?,
    val hasPrayed: Boolean,
    val nextEventTime: LocalTime?,
    val nextEventLabel: String,
    val fajrTime: LocalTime?,
    val sunriseTime: LocalTime?,
    val sunsetTime: LocalTime?,
    val ishaTime: LocalTime?,
    val currentPrayerEndTime: LocalTime?
)