package com.example.awwal.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun AppNavGraph() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary
        ),
    )


    NavigationSuiteScaffold(
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Color.Transparent,
        ),
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                    colors = myNavigationSuiteItemColors
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> AppDestinations.HOME.content()
            AppDestinations.STATISTICS -> AppDestinations.STATISTICS.content()
            AppDestinations.SETTINGS -> AppDestinations.SETTINGS.content()
        }
    }
}