package com.example.awwal.data

import android.content.Context
import com.example.awwal.data.local.DatabaseProvider
import com.example.awwal.data.local.entity.PrayerDataEntity
import com.example.awwal.domain.PrayerRepository
import com.example.awwal.domain.classes.PrayerData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class PrayerRepositoryImpl(private val context: Context): PrayerRepository {
    private val database = DatabaseProvider.getDatabase(context)
    private val prayerDao = database.prayerDao()

    override suspend fun savePrayerData(prayerData: PrayerData) {
        val entity = prayerData.toEntity()
        prayerDao.insertPrayer(entity)
    }

    // Additional helper methods for better usability
    override suspend fun savePrayersForDay(date: LocalDate, prayers: List<PrayerData>) {
        val entities = prayers.map { it.toEntity() }
        prayerDao.insertPrayers(entities)
    }

    override fun getPrayersForDateFlow(date: LocalDate): Flow<List<PrayerData>> {
        return prayerDao.getPrayersForDate(date).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun updatePrayerStatus(prayerData: PrayerData) {
        // Use savePrayerData which does INSERT OR REPLACE
        // This works whether the prayer exists or not (upsert behavior)
        savePrayerData(prayerData)
    }

    override suspend fun deletePrayersForDate(date: LocalDate) {
        prayerDao.deletePrayersForDate(date)
    }

    // Mapper functions
    private fun PrayerDataEntity.toDomainModel(): PrayerData {
        return PrayerData(
            prayerName = this.prayerName,
            date = this.date,
            prayerStatus = this.status,
            timePrayed = this.timePrayed,
            prayerWindowPercentage = this.prayerWindowPercentage
        )
    }

    private fun PrayerData.toEntity(): PrayerDataEntity {
        return PrayerDataEntity(
            prayerName = this.prayerName,
            date = this.date,
            status = this.prayerStatus,
            timePrayed = this.timePrayed,
            prayerWindowPercentage = this.prayerWindowPercentage
        )
    }
}