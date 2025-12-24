package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.common.DateNavigator
import com.example.awwal.presentation.ui.screens.home.components.PrayerItem
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

data class Prayer(
    val name: String,
    val time: String = "-- : --",
    val isCompleted: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel()
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val prayersFromDb by viewModel.prayersForDate.collectAsState()

    // Load prayers when date changes
    LaunchedEffect(currentDate) {
        viewModel.loadPrayersForDate(currentDate)
    }

    // TODO: Replace with actual prayer times from API
    val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
    val prayerTimes = mapOf(
        "Fajr" to "05:30 AM",
        "Dhuhr" to "12:15 PM",
        "Asr" to "03:45 PM",
        "Maghrib" to "06:20 PM",
        "Isha" to "08:00 PM"
    )

    // Create prayer map for quick lookup
    val prayerStatusMap = remember(prayersFromDb) {
        prayersFromDb.associateBy { it.prayerName }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DateNavigator(
                    currentDate = currentDate,
                    onPreviousDay = { currentDate = currentDate.minusDays(1) },
                    onNextDay = { currentDate = currentDate.plusDays(1) }
                )
            }

            // Prayer Section Title
            item {
                Text(
                    text = "Daily Prayers",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Prayer Items
            items(prayerNames) { prayerName ->
                val isCompleted = prayerStatusMap[prayerName]?.prayerStatus?.name == "PRAYED"

                PrayerItem(
                    prayerName = prayerName,
                    prayerTime = prayerTimes[prayerName] ?: "-- : --",
                    checked = isCompleted,
                    onCheckedChange = { isChecked ->
                        // Update prayer completion in database for the selected date
                        viewModel.updatePrayerStatus(
                            prayerName = prayerName,
                            date = currentDate,
                            newStatus = if (isChecked)
                                com.example.awwal.domain.classes.enums.PrayerStatus.PRAYED
                            else
                                com.example.awwal.domain.classes.enums.PrayerStatus.EMPTY
                        )
                    }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
