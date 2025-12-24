package com.example.awwal.domain

import com.example.awwal.domain.classes.PrayerData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PrayerRepository {
    suspend fun fetchPrayerForDay(date: String): PrayerData

    suspend fun savePrayerData(prayerData: PrayerData)

    // Additional methods for better usability
    suspend fun savePrayersForDay(date: LocalDate, prayers: List<PrayerData>)

    fun getPrayersForDateFlow(date: LocalDate): Flow<List<PrayerData>>

    suspend fun updatePrayerStatus(prayerData: PrayerData)

    suspend fun deletePrayersForDate(date: LocalDate)
}

