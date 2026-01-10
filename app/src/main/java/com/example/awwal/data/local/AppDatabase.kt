package com.example.awwal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.awwal.data.local.dao.PrayerDao
import com.example.awwal.data.local.entity.PrayerDataEntity

@Database(
    entities = [PrayerDataEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
}

