package com.example.awwal

import android.app.Application
import com.example.awwal.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AwwalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin for Dependency Injection
        startKoin {
            androidLogger(Level.ERROR) // Only show errors in logs
            androidContext(this@AwwalApplication)
            modules(appModules)
        }
    }
}

