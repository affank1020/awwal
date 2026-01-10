package com.example.awwal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awwal.domain.PrayerTimesRepository
import com.example.awwal.domain.classes.CalculationMethodType
import com.example.awwal.domain.classes.MadhabType
import com.example.awwal.domain.classes.PrayerTimeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrayerTimesSettingsViewModel(
    private val prayerTimesRepository: PrayerTimesRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(PrayerTimeSettings())
    val settings: StateFlow<PrayerTimeSettings> = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            prayerTimesRepository.getSettings().collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            prayerTimesRepository.updateLocation(latitude, longitude)
            _isLoading.value = false
        }
    }

    fun updateCalculationMethod(method: CalculationMethodType) {
        viewModelScope.launch {
            _isLoading.value = true
            prayerTimesRepository.updateCalculationMethod(method)
            _isLoading.value = false
        }
    }

    fun updateMadhab(madhab: MadhabType) {
        viewModelScope.launch {
            _isLoading.value = true
            prayerTimesRepository.updateMadhab(madhab)
            _isLoading.value = false
        }
    }

    fun updateSettings(settings: PrayerTimeSettings) {
        viewModelScope.launch {
            _isLoading.value = true
            prayerTimesRepository.updateSettings(settings)
            _isLoading.value = false
        }
    }
}

