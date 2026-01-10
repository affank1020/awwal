package com.example.awwal.domain

import com.example.awwal.domain.classes.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing app theme settings
 * COULD BE DEPRECATED
 */
interface ThemeRepository {
    val themeFlow: Flow<ThemeMode>

    /**
     * Set the app theme
     */
    suspend fun setTheme(theme: ThemeMode)
}