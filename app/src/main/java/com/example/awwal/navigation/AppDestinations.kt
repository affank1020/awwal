package com.example.awwal.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.awwal.presentation.ui.screens.home.HomeScreen
import com.example.awwal.presentation.ui.screens.statistics.StatisticsScreen
import com.example.awwal.presentation.ui.screens.settings.SettingsScreen

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit
) {
    HOME("Home", Icons.Default.Home, { HomeScreen() }),
    STATISTICS("Statistics", Icons.Default.AccountBox, { StatisticsScreen() }),
    SETTINGS("Settings", Icons.Default.Settings, { SettingsScreen() })
}