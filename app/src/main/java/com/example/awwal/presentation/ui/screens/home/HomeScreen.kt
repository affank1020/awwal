package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.date.DateNavigator
import com.example.awwal.presentation.ui.screens.home.components.prayerItem.PrayerItem
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val totalPages = 10000 // Large number to allow extensive scrolling
    val todayPage = totalPages / 2
    val pagerState = rememberPagerState(initialPage = todayPage) { todayPage + 1 } // Only allow up to today

    fun pageToDate(page: Int): LocalDate = LocalDate.now().plusDays((page - todayPage).toLong())
    fun dateToPage(date: LocalDate): Int = todayPage + LocalDate.now().until(date).days

    // Cache prayer data for multiple pages
    val prayerCache = remember { mutableStateMapOf<LocalDate, List<PrayerData>>() }

    // Collect from ViewModel and update cache
    val prayersFromDb by viewModel.prayersForDate.collectAsState()
    val currentLoadedDate by viewModel.currentLoadedDate.collectAsState()

    // Update cache when data is loaded
    LaunchedEffect(prayersFromDb, currentLoadedDate) {
        currentLoadedDate?.let { date ->
            prayerCache[date] = prayersFromDb
        }
    }

    // Preload adjacent pages
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                // Load current page
                val currentDate = pageToDate(page)
                viewModel.loadPrayersForDate(currentDate)

                // Preload previous and next pages
                val prevDate = pageToDate(page - 1)
                val nextDate = pageToDate(page + 1)
                if (!prayerCache.containsKey(prevDate)) {
                    viewModel.loadPrayersForDateIntoCache(prevDate) { prayers ->
                        prayerCache[prevDate] = prayers
                    }
                }
                if (!prayerCache.containsKey(nextDate)) {
                    viewModel.loadPrayersForDateIntoCache(nextDate) { prayers ->
                        prayerCache[nextDate] = prayers
                    }
                }
            }
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                    startY = 1000f,
                )
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageDate = pageToDate(page)

            // Get cached prayer data for this page
            val pagePrayers = prayerCache[pageDate] ?: emptyList()
            val prayerStatusMap = pagePrayers.associateBy { it.prayerName }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DateNavigator(
                        currentDate = pageDate,
                        showNext = page < todayPage,
                        onPrevious = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    (page - 1).coerceAtLeast(0),
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }
                        },
                        onNext = if (page < todayPage) {
                            {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        (page + 1).coerceAtMost(todayPage),
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                }
                            }
                        } else null,
                        onDateSelected = { selectedDate ->
                            val targetPage = dateToPage(selectedDate).coerceAtMost(todayPage)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    targetPage,
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }
                        },
                    )
                }

                items(prayerNames) { prayerName ->
                    PrayerItem(
                        prayerName = prayerName,
                        prayerTime = prayerTimes[prayerName] ?: "-- : --",
                        currentStatus = prayerStatusMap[prayerName]?.prayerStatus
                            ?: PrayerStatus.EMPTY,
                        onStatusChange = { newStatus ->
                            viewModel.updatePrayerStatus(
                                prayerName = prayerName,
                                date = pageDate,
                                newStatus = newStatus
                            )
                            // Update cache immediately for responsive UI
                            val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
                            val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
                            val updatedPrayer = PrayerData(prayerName, pageDate, newStatus)
                            if (existingIndex >= 0) {
                                currentList[existingIndex] = updatedPrayer
                            } else {
                                currentList.add(updatedPrayer)
                            }
                            prayerCache[pageDate] = currentList
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
