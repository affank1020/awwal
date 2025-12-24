package com.example.awwal.data.local.dao

import androidx.room.*
import com.example.awwal.data.local.entity.PrayerDataEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_data WHERE date = :date ORDER BY prayerName")
    fun getPrayersForDate(date: LocalDate): Flow<List<PrayerDataEntity>>

    @Query("SELECT * FROM prayer_data WHERE date = :date")
    suspend fun getPrayersForDateOnce(date: LocalDate): List<PrayerDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayer(prayer: PrayerDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayers(prayers: List<PrayerDataEntity>)

    @Update
    suspend fun updatePrayer(prayer: PrayerDataEntity)

    @Delete
    suspend fun deletePrayer(prayer: PrayerDataEntity)

    @Query("DELETE FROM prayer_data WHERE date = :date")
    suspend fun deletePrayersForDate(date: LocalDate)

    @Query("SELECT * FROM prayer_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date, prayerName")
    fun getPrayersInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PrayerDataEntity>>

    @Query("DELETE FROM prayer_data")
    suspend fun deleteAllPrayers()
}

