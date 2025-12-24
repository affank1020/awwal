package com.example.awwal.di

import androidx.room.Room
import com.example.awwal.data.PrayerRepositoryImpl
import com.example.awwal.data.ThemeRepositoryImpl
import com.example.awwal.data.local.AppDatabase
import com.example.awwal.domain.PrayerRepository
import com.example.awwal.domain.ThemeRepository
import com.example.awwal.presentation.viewmodel.PrayersViewModel
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
}

/**
 * Koin module for ViewModels
 */
val viewModelModule = module {
    viewModel { PrayersViewModel(get()) }
}

/**
 * All app modules combined
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)

