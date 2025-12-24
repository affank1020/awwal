package com.example.awwal.domain

import com.example.awwal.domain.classes.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeFlow: Flow<ThemeMode>
    suspend fun setTheme(theme: ThemeMode)
}