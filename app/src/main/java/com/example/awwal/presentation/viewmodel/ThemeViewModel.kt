package com.example.awwal.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.awwal.data.ThemeRepositoryImpl
import com.example.awwal.domain.classes.enums.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ThemeRepositoryImpl(application)

    val themeState: StateFlow<ThemeMode> = repository.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.AUTO
        )

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }
}