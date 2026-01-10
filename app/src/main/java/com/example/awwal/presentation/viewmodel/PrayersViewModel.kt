package com.example.awwal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awwal.domain.PrayerRepository
import com.example.awwal.domain.PrayerTimesRepository
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.PrayerTimes
import com.example.awwal.domain.classes.enums.PrayerStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class PrayersViewModel(
    private val prayerRepository: PrayerRepository,
    private val prayerTimesRepository: PrayerTimesRepository
) : ViewModel() {

    private val _prayersForDate = MutableStateFlow<List<PrayerData>>(emptyList())
    val prayersForDate: StateFlow<List<PrayerData>> = _prayersForDate.asStateFlow()

    private val _currentLoadedDate = MutableStateFlow<LocalDate?>(null)
    val currentLoadedDate: StateFlow<LocalDate?> = _currentLoadedDate.asStateFlow()

    private val _prayerTimes = MutableStateFlow<PrayerTimes?>(null)

    private val _prayerTimesMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val prayerTimesMap: StateFlow<Map<String, String>> = _prayerTimesMap.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    private val _error = MutableStateFlow<String?>(null)

    private var loadPrayersJob: kotlinx.coroutines.Job? = null

    // Load prayers for a specific date
    fun loadPrayersForDate(date: LocalDate) {
        // Cancel previous collection to prevent multiple Flows from racing
        loadPrayersJob?.cancel()

        loadPrayersJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _currentLoadedDate.value = date
            try {
                // Load prayer times from Adhan
                val times = prayerTimesRepository.getPrayerTimesForDate(date)
                _prayerTimes.value = times
                _prayerTimesMap.value = times.toFormattedMap()

                // Load prayer status from database
                prayerRepository.getPrayersForDateFlow(date).collect { prayers ->
                    _prayersForDate.value = prayers
                }
            } catch (e: Exception) {
                _error.value = "Failed to load prayers: ${e.message}"
                _prayersForDate.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get prayer times for a specific date (for caching/preloading)
    fun getPrayerTimesForDate(date: LocalDate): Map<String, String> {
        return try {
            prayerTimesRepository.getPrayerTimesForDate(date).toFormattedMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // Load prayers for a date into a cache callback (for preloading adjacent pages)
    fun loadPrayersForDateIntoCache(date: LocalDate, onLoaded: (List<PrayerData>) -> Unit) {
        viewModelScope.launch {
            try {
                prayerRepository.getPrayersForDateFlow(date).collect { prayers ->
                    onLoaded(prayers)
                }
            } catch (e: Exception) {
                onLoaded(emptyList())
            }
        }
    }

    // Update prayer status (e.g., when user checks off a prayer)
    fun updatePrayerStatus(prayerName: String, date: LocalDate, newStatus: PrayerStatus) {
        viewModelScope.launch {
            try {
                val updatedPrayer = PrayerData(prayerName, date, newStatus)
                prayerRepository.updatePrayerStatus(updatedPrayer)
            } catch (e: Exception) {
                _error.value = "Failed to update prayer: ${e.message}"
            }
        }
    }

    /**
     * Update prayer status with time prayed and calculate window percentage
     * @param isNextDay If true, the time prayed is after midnight (for Isha prayers)
     */
    fun updatePrayerStatusWithTime(
        prayerName: String,
        date: LocalDate,
        newStatus: PrayerStatus,
        timePrayed: LocalTime?,
        isNextDay: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val windowPercentage = if (timePrayed != null && newStatus == PrayerStatus.PRAYED) {
                    calculatePrayerWindowPercentage(prayerName, date, timePrayed, isNextDay)
                } else null

                val updatedPrayer = PrayerData(
                    prayerName = prayerName,
                    date = date,
                    prayerStatus = newStatus,
                    timePrayed = if (newStatus == PrayerStatus.PRAYED) timePrayed else null,
                    prayerWindowPercentage = windowPercentage
                )
                prayerRepository.updatePrayerStatus(updatedPrayer)
            } catch (e: Exception) {
                _error.value = "Failed to update prayer: ${e.message}"
            }
        }
    }

    /**
     * Calculate what percentage of the prayer window had elapsed when the user prayed
     * 0.0 = prayed at the start, 1.0 = prayed at the end of the window
     * @param isNextDay If true, the time prayed is after midnight (for Isha prayers)
     */
    private fun calculatePrayerWindowPercentage(
        prayerName: String,
        date: LocalDate,
        timePrayed: LocalTime,
        isNextDay: Boolean = false
    ): Float? {
        return try {
            val times = prayerTimesRepository.getPrayerTimesForDate(date)
            val startTime = times.getTimeForPrayer(prayerName) ?: return null

            // For Isha, end time is Fajr of next day
            val endTime = if (prayerName.equals("Isha", ignoreCase = true)) {
                val nextDayTimes = prayerTimesRepository.getPrayerTimesForDate(date.plusDays(1))
                nextDayTimes.fajr
            } else {
                times.getEndTimeForPrayer(prayerName) ?: return null
            }

            // Calculate window duration
            val windowDuration = if (prayerName.equals("Isha", ignoreCase = true)) {
                // Isha spans midnight
                val minutesToMidnight = Duration.between(startTime, LocalTime.MAX).toMinutes() + 1
                val minutesAfterMidnight = Duration.between(LocalTime.MIDNIGHT, endTime).toMinutes()
                (minutesToMidnight + minutesAfterMidnight).toFloat()
            } else {
                Duration.between(startTime, endTime).toMinutes().toFloat()
            }

            if (windowDuration <= 0) return null

            // Calculate elapsed duration
            val elapsedDuration = if (isNextDay) {
                // Time is after midnight
                val minutesToMidnight = Duration.between(startTime, LocalTime.MAX).toMinutes() + 1
                val minutesAfterMidnight = Duration.between(LocalTime.MIDNIGHT, timePrayed).toMinutes()
                (minutesToMidnight + minutesAfterMidnight).toFloat()
            } else {
                Duration.between(startTime, timePrayed).toMinutes().toFloat()
            }

            (elapsedDuration / windowDuration).coerceIn(0f, 1f)
        } catch (e: Exception) {
            null
        }
    }
}