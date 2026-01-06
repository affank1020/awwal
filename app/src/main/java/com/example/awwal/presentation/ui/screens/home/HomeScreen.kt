package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.presentation.ui.common.date.DateNavigator
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.ui.common.date.datePager.DatePager
import com.example.awwal.presentation.ui.screens.home.components.PrayersList
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
    val pagingState = remember { DatePagingState() }
    val prayerCache = remember { mutableStateMapOf<LocalDate, List<PrayerData>>() }
    val prayersFromDb by viewModel.prayersForDate.collectAsState()
    val currentLoadedDate by viewModel.currentLoadedDate.collectAsState()

    LaunchedEffect(prayersFromDb, currentLoadedDate) {
        currentLoadedDate?.let { date ->
            prayerCache[date] = prayersFromDb
        }
    }

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
        DatePager(
            totalPages = pagingState.totalPages,
            todayPage = pagingState.todayPage,
            pageToDate = { pagingState.pageToDate(it) },
            dateToPage = { pagingState.dateToPage(it) },
            modifier = Modifier.fillMaxSize()
        ) { pageDate, page, pagerState ->
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(page) {
                val currentDate = pagingState.pageToDate(page)
                viewModel.loadPrayersForDate(currentDate)
                val prevDate = pagingState.pageToDate((page - 1).coerceAtLeast(0))
                val nextDate = pagingState.pageToDate((page + 1).coerceAtMost(pagingState.totalPages - 1))
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

            val pagePrayers = prayerCache[pageDate] ?: emptyList()
            val prayerStatusMap = pagePrayers.associateBy { it.prayerName }

            Column(Modifier.fillMaxSize()) {
                DateNavigator(
                    currentDate = pageDate,
                    onPrevious = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                (page - 1).coerceAtLeast(0),
                                animationSpec = tween(durationMillis = 500)
                            )
                        }
                    },
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                (page + 1).coerceAtMost(pagingState.totalPages - 1),
                                animationSpec = tween(durationMillis = 500)
                            )
                        }
                    },
                    onDateSelected = { selectedDate ->
                        val targetPage = pagingState.dateToPage(selectedDate).coerceIn(0, pagingState.totalPages - 1)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                targetPage,
                                animationSpec = tween(durationMillis = 500)
                            )
                        }
                    },
                    futureDates = false
                )
                PrayersList(
                    prayerNames = prayerNames,
                    prayerTimes = prayerTimes,
                    prayerStatusMap = prayerStatusMap,
                    onStatusChange = { prayerName, newStatus ->
                        viewModel.updatePrayerStatus(
                            prayerName = prayerName,
                            date = pageDate,
                            newStatus = newStatus
                        )
                        val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
                        val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
                        val updatedPrayer = PrayerData(prayerName, pageDate, newStatus)
                        if (existingIndex >= 0) {
                            currentList[existingIndex] = updatedPrayer
                        } else {
                            currentList.add(updatedPrayer)
                        }
                        prayerCache[pageDate] = currentList
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
