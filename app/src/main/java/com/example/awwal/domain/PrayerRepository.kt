package com.example.awwal.domain

import com.example.awwal.domain.classes.PrayerData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for managing prayer data
 */
interface PrayerRepository {

    /**
     * Save a single prayer data entry
     */
    suspend fun savePrayerData(prayerData: PrayerData)

    /**
     * Save multiple prayers for a specific day
     */
    suspend fun savePrayersForDay(date: LocalDate, prayers: List<PrayerData>)

    /**
     * Get prayers for a specific date as a Flow for reactive updates
     */
    fun getPrayersForDateFlow(date: LocalDate): Flow<List<PrayerData>>

    /**
     * Update the status of a specific prayer
     */
    suspend fun updatePrayerStatus(prayerData: PrayerData)

    /**
     * Delete all prayers for a specific date
     */
    suspend fun deletePrayersForDate(date: LocalDate)
}

