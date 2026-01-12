package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.date.dateNavigator.DateNavigator
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.ui.common.date.datePager.DatePager
import com.example.awwal.presentation.ui.screens.home.components.PrayersList
import com.example.awwal.presentation.ui.screens.home.components.mainWidget.MainWidget
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import java.time.LocalDate
import java.time.LocalTime
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
    var calendarVisible by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(java.time.YearMonth.now()) }
    var isListAtTop by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

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
            modifier = Modifier.fillMaxSize()
        ) { pageDate, page, pagerState ->
            LaunchedEffect(page) {
                val currentDate = pagingState.pageToDate(page)
                viewModel.loadPrayersForDate(currentDate)
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
                if (!prayerTimesCache.containsKey(prevDate)) {
                    prayerTimesCache[prevDate] = viewModel.getPrayerTimesForDate(prevDate)
                }
                if (!prayerTimesCache.containsKey(nextPageDate)) {
                    prayerTimesCache[nextPageDate] = viewModel.getPrayerTimesForDate(nextPageDate)
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
                            pagerState.animateScrollToPage((page - 1).coerceAtLeast(0), animationSpec = tween(durationMillis = 500))
                        }
                    },
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage((page + 1).coerceAtMost(pagingState.totalPages - 1), animationSpec = tween(durationMillis = 500))
                        }
                    },
                    onDateSelected = { selectedDate ->
                        val targetPage = pagingState.dateToPage(selectedDate).coerceIn(0, pagingState.totalPages - 1)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetPage, animationSpec = tween(durationMillis = 500))
                        }
                    },
                    futureDates = false,
                    isToday = page == pagingState.todayPage
                )
                Box(Modifier.fillMaxSize()) {
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
                            val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
                            val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
                            val updatedPrayer = PrayerData(
                                prayerName = prayerName,
                                date = pageDate,
                                prayerStatus = newStatus,
                                timePrayed = null,
                                prayerWindowPercentage = null
                            )
                            if (existingIndex >= 0) {
                                currentList[existingIndex] = updatedPrayer
                            } else {
                                currentList.add(updatedPrayer)
                            }
                            prayerCache[pageDate] = currentList
                        },
                        onStatusChangeWithTime = { prayerName, newStatus, timePrayed ->
                            viewModel.updatePrayerStatusWithTime(
                                prayerName = prayerName,
                                date = pageDate,
                                newStatus = newStatus,
                                timePrayed = timePrayed
                            )
                            val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
                            val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
                            val updatedPrayer = PrayerData(
                                prayerName = prayerName,
                                date = pageDate,
                                prayerStatus = newStatus,
                                timePrayed = timePrayed,
                                prayerWindowPercentage = null
                            )
                            if (existingIndex >= 0) {
                                currentList[existingIndex] = updatedPrayer
                            } else {
                                currentList.add(updatedPrayer)
                            }
                            prayerCache[pageDate] = currentList
                        },
                        modifier = Modifier.fillMaxSize(),
                        onScrollStateChanged = { atTop ->
                            isListAtTop = atTop
                        },
                        onOverscrollTop = {
                            calendarVisible = true
                        },
                        headerContent = {
                            if (page == pagingState.todayPage) {
                                MainWidget(
                                    prayerNames = prayerNames,
                                    prayerTimes = pagePrayerTimes,
                                    prayerStatusMap = prayerStatusMap,
                                    currentDate = pageDate,
                                    onStatusChange = { prayerName, newStatus, timePrayed ->
                                        viewModel.updatePrayerStatusWithTime(
                                            prayerName = prayerName,
                                            date = pageDate,
                                            newStatus = newStatus,
                                            timePrayed = timePrayed
                                        )
                                        val currentList = prayerCache[pageDate]?.toMutableList() ?: mutableListOf()
                                        val existingIndex = currentList.indexOfFirst { it.prayerName == prayerName }
                                        val updatedPrayer = PrayerData(
                                            prayerName = prayerName,
                                            date = pageDate,
                                            prayerStatus = newStatus,
                                            timePrayed = timePrayed,
                                            prayerWindowPercentage = null
                                        )
                                        if (existingIndex >= 0) {
                                            currentList[existingIndex] = updatedPrayer
                                        } else {
                                            currentList.add(updatedPrayer)
                                        }
                                        prayerCache[pageDate] = currentList
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    )
                    if (calendarVisible) {
                        // Example: CalendarOverlay(...)
                    }
                }
            }
        }
    }
}
