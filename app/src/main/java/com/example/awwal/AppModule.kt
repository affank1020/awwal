package com.example.awwal

import androidx.room.Room
import com.example.awwal.data.PrayerRepositoryImpl
import com.example.awwal.data.PrayerTimesRepositoryImpl
import com.example.awwal.data.ThemeRepositoryImpl
import com.example.awwal.data.local.AppDatabase
import com.example.awwal.domain.PrayerRepository
import com.example.awwal.domain.PrayerTimesRepository
import com.example.awwal.domain.ThemeRepository
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import com.example.awwal.presentation.viewmodel.PrayerTimesSettingsViewModel
import com.example.awwal.presentation.viewmodel.ThemeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for database dependencies
 */
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "awwal_database"
        )
            .fallbackToDestructiveMigration() // For development - remove in production
            .build()
    }

    single { get<AppDatabase>().prayerDao() }
}

/**
 * Koin module for repositories
 */
val repositoryModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl(androidContext()) }
    single<ThemeRepository> { ThemeRepositoryImpl(androidContext()) }
    single<PrayerTimesRepository> { PrayerTimesRepositoryImpl(androidContext()) }
}

/**
 * Koin module for ViewModels
 */
val viewModelModule = module {
    viewModel { PrayersViewModel(get(), get()) }
    viewModel { PrayerTimesSettingsViewModel(get()) }
    viewModel { ThemeViewModel(androidApplication()) }
}

/**
 * All app modules combined
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)
