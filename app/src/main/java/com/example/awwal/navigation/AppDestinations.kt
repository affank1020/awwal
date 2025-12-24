package com.example.awwal.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.awwal.presentation.ui.screens.home.HomeScreen
import com.example.awwal.presentation.ui.screens.statistics.StatisticsScreen
import com.example.awwal.presentation.ui.screens.settings.SettingsScreen

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit,
) {
    HOME("Home", Icons.Default.Home, { HomeScreen(modifier = Modifier.fillMaxSize()) }),
    STATISTICS("Statistics", Icons.Default.AccountBox, { StatisticsScreen(modifier = Modifier.fillMaxSize()) }),
    SETTINGS("Settings", Icons.Default.Settings, { SettingsScreen(modifier = Modifier.fillMaxSize()) }),
}