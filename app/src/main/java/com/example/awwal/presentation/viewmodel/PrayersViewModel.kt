package com.example.awwal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awwal.domain.PrayerRepository
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Shared ViewModel for managing prayer data across multiple screens
 * Used by: HomeScreen, StatisticsScreen, and any other screen that needs prayer data
 */
class PrayersViewModel(
    private val prayerRepository: PrayerRepository
) : ViewModel() {

    private val _prayersForDate = MutableStateFlow<List<PrayerData>>(emptyList())
    val prayersForDate: StateFlow<List<PrayerData>> = _prayersForDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Load prayers for a specific date
    fun loadPrayersForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
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

    // Load prayers for today (convenience method for HomeScreen)
    fun loadTodaysPrayers() {
        loadPrayersForDate(LocalDate.now())
    }

    // Save a single prayer
    fun savePrayer(prayerData: PrayerData) {
        viewModelScope.launch {
            try {
                prayerRepository.savePrayerData(prayerData)
            } catch (e: Exception) {
                _error.value = "Failed to save prayer: ${e.message}"
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

    // Toggle prayer completion for today (used in HomeScreen checkboxes)
    fun togglePrayerCompletion(prayerName: String, isCompleted: Boolean) {
        val status = if (isCompleted) PrayerStatus.PRAYED else PrayerStatus.EMPTY
        updatePrayerStatus(prayerName, LocalDate.now(), status)
    }

    // Save all 5 prayers for a day
    fun savePrayersForDay(date: LocalDate, prayers: List<PrayerData>) {
        viewModelScope.launch {
            try {
                prayerRepository.savePrayersForDay(date, prayers)
            } catch (e: Exception) {
                _error.value = "Failed to save prayers: ${e.message}"
            }
        }
    }

    // Delete prayers for a specific date
    fun deletePrayersForDate(date: LocalDate) {
        viewModelScope.launch {
            try {
                prayerRepository.deletePrayersForDate(date)
            } catch (e: Exception) {
                _error.value = "Failed to delete prayers: ${e.message}"
            }
        }
    }

    // Get a specific prayer for a date
    fun getPrayerByName(prayerName: String, date: LocalDate): PrayerData? {
        return _prayersForDate.value.find {
            it.prayerName == prayerName && it.date == date
        }
    }

    // Initialize with mock data for testing
    fun initializeMockData(date: LocalDate) {
        val mockPrayers = listOf(
            PrayerData("Fajr", date, PrayerStatus.PRAYED),
            PrayerData("Dhuhr", date, PrayerStatus.PRAYED),
            PrayerData("Asr", date, PrayerStatus.LATE),
            PrayerData("Maghrib", date, PrayerStatus.PRAYED),
            PrayerData("Isha", date, PrayerStatus.MISSED)
        )
        savePrayersForDay(date, mockPrayers)
    }

    // Clear error message
    fun clearError() {
        _error.value = null
    }
}