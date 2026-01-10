package com.example.awwal.domain

import com.example.awwal.domain.classes.CalculationMethodType
import com.example.awwal.domain.classes.MadhabType
import com.example.awwal.domain.classes.PrayerTimeSettings
import com.example.awwal.domain.classes.PrayerTimes
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for prayer times calculations and settings
 */
interface PrayerTimesRepository {

    /**
     * Get prayer times for a specific date
     */
    fun getPrayerTimesForDate(date: LocalDate): PrayerTimes

    /**
     * Get current prayer time settings
     */
    fun getSettings(): Flow<PrayerTimeSettings>

    /**
     * Update prayer time settings
     */
    suspend fun updateSettings(settings: PrayerTimeSettings)

    /**
     * Update location
     */
    suspend fun updateLocation(latitude: Double, longitude: Double)

    /**
     * Update calculation method
     */
    suspend fun updateCalculationMethod(method: CalculationMethodType)

    /**
     * Update madhab
     */
    suspend fun updateMadhab(madhab: MadhabType)
}

