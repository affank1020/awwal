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
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.date.dateNavigator.DateNavigator
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.ui.common.date.datePager.DatePager
import com.example.awwal.presentation.ui.screens.home.components.PrayersList
import com.example.awwal.presentation.ui.screens.home.components.CurrentPrayerWidget
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel()
) {
    val pagingState = remember { DatePagingState() }
    val prayerCache = remember { mutableStateMapOf<LocalDate, List<PrayerData>>() }
    val prayerTimesCache = remember { mutableStateMapOf<LocalDate, Map<String, String>>() }
    val prayersFromDb by viewModel.prayersForDate.collectAsState()
    val currentLoadedDate by viewModel.currentLoadedDate.collectAsState()
    val prayerTimesMap by viewModel.prayerTimesMap.collectAsState()

    LaunchedEffect(prayersFromDb, currentLoadedDate) {
        currentLoadedDate?.let { date ->
            prayerCache[date] = prayersFromDb
        }
    }

    LaunchedEffect(prayerTimesMap, currentLoadedDate) {
        currentLoadedDate?.let { date ->
            if (prayerTimesMap.isNotEmpty()) {
                prayerTimesCache[date] = prayerTimesMap
            }
        }
    }

    val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Helper function to update cache after status change
    fun updatePrayerInCache(pageDate: LocalDate, prayerName: String, newStatus: PrayerStatus, timePrayed: LocalTime?) {
        val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
        val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
        val updatedPrayer = PrayerData(
            prayerName = prayerName,
            date = pageDate,
            prayerStatus = newStatus,
            timePrayed = if (newStatus == PrayerStatus.PRAYED) timePrayed else null,
            prayerWindowPercentage = null // Will be calculated in ViewModel
        )
        if (existingIndex >= 0) {
            currentList[existingIndex] = updatedPrayer
        } else {
            currentList.add(updatedPrayer)
        }
        prayerCache[pageDate] = currentList
    }

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

            // Get next day's Fajr time for Isha validation
            val nextDate = pageDate.plusDays(1)
            val nextDayPrayerTimes = prayerTimesCache[nextDate] ?: viewModel.getPrayerTimesForDate(nextDate)
            val nextDayFajrTime = nextDayPrayerTimes["Fajr"]?.let {
                try { LocalTime.parse(it, timeFormatter) } catch (e: Exception) { null }
            }

            LaunchedEffect(page) {
                val currentDate = pagingState.pageToDate(page)
                viewModel.loadPrayersForDate(currentDate)

                // Preload prayer times for adjacent pages
                val prevDate = pagingState.pageToDate((page - 1).coerceAtLeast(0))
                val nextPageDate = pagingState.pageToDate((page + 1).coerceAtMost(pagingState.totalPages - 1))

                if (!prayerCache.containsKey(prevDate)) {
                    viewModel.loadPrayersForDateIntoCache(prevDate) { prayers ->
                        prayerCache[prevDate] = prayers
                    }
                }
                if (!prayerCache.containsKey(nextPageDate)) {
                    viewModel.loadPrayersForDateIntoCache(nextPageDate) { prayers ->
                        prayerCache[nextPageDate] = prayers
                    }
                }

                // Cache prayer times for adjacent pages
                if (!prayerTimesCache.containsKey(prevDate)) {
                    prayerTimesCache[prevDate] = viewModel.getPrayerTimesForDate(prevDate)
                }
                if (!prayerTimesCache.containsKey(nextPageDate)) {
                    prayerTimesCache[nextPageDate] = viewModel.getPrayerTimesForDate(nextPageDate)
                }
                // Also cache next day for Isha validation
                if (!prayerTimesCache.containsKey(nextDate)) {
                    prayerTimesCache[nextDate] = viewModel.getPrayerTimesForDate(nextDate)
                }
            }

            val showPrayerTimes = page == pagingState.todayPage
            val pagePrayerTimes = if (showPrayerTimes) {
                prayerTimesCache[pageDate] ?: viewModel.getPrayerTimesForDate(pageDate)
            } else {
                emptyMap()
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
                    futureDates = false,
                    isToday = page == pagingState.todayPage
                )
                PrayersList(
                    prayerNames = prayerNames,
                    prayerTimes = pagePrayerTimes,
                    prayerStatusMap = prayerStatusMap,
                    onStatusChange = { prayerName, newStatus ->
                        viewModel.updatePrayerStatus(
                            prayerName = prayerName,
                            date = pageDate,
                            newStatus = newStatus
                        )
                        updatePrayerInCache(pageDate, prayerName, newStatus, null)
                    },
                    onStatusChangeWithTime = { prayerName, newStatus, timePrayed, isNextDay ->
                        viewModel.updatePrayerStatusWithTime(
                            prayerName = prayerName,
                            date = pageDate,
                            newStatus = newStatus,
                            timePrayed = timePrayed,
                            isNextDay = isNextDay
                        )
                        updatePrayerInCache(pageDate, prayerName, newStatus, timePrayed)
                    },
                    nextDayFajrTime = nextDayFajrTime,
                    modifier = Modifier.weight(1f),
                    headerContent = if (page == pagingState.todayPage) {
                        {
                            CurrentPrayerWidget(
                                prayerNames = prayerNames,
                                prayerTimes = pagePrayerTimes,
                                prayerStatusMap = prayerStatusMap,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                currentDate = pageDate,
                                nextDayFajrTime = nextDayFajrTime,
                                onStatusChange = { prayerName, newStatus, timePrayed, isNextDay ->
                                    viewModel.updatePrayerStatusWithTime(
                                        prayerName = prayerName,
                                        date = pageDate,
                                        newStatus = newStatus,
                                        timePrayed = timePrayed,
                                        isNextDay = isNextDay
                                    )
                                    updatePrayerInCache(pageDate, prayerName, newStatus, timePrayed)
                                }
                            )
                        }
                    } else null
                )
            }
        }
    }
}
