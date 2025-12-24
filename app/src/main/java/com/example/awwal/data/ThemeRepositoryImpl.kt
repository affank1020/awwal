package com.example.awwal.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.awwal.domain.ThemeRepository
import com.example.awwal.domain.classes.enums.ThemeMode
import com.example.awwal.domain.classes.enums.enumFromValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeRepositoryImpl(private val context: Context): ThemeRepository {
    private val THEME_KEY = intPreferencesKey("theme_mode")

    override val themeFlow: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            val themeValue = preferences[THEME_KEY] ?: ThemeMode.AUTO.value
            enumFromValue<ThemeMode>(themeValue)
        }

    override suspend fun setTheme(theme: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.value
        }
    }
}