package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.calendar.CalendarBottomSheet
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.ui.screens.home.components.DateNavigationRow
import com.example.awwal.presentation.ui.screens.home.components.PrayersList
import com.example.awwal.presentation.ui.screens.home.components.mainWidget.MainWidget
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val coroutineScope = rememberCoroutineScope()

    // Bottom sheet state for calendar
    val calendarSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Load prayer data for visible month when calendar opens
    LaunchedEffect(calendarVisible, currentMonth) {
        if (calendarVisible) {
            val firstDay = currentMonth.atDay(1)
            val lastDay = currentMonth.atEndOfMonth()
            var day = firstDay
            while (!day.isAfter(lastDay)) {
                if (!prayerCache.containsKey(day)) {
                    viewModel.loadPrayersForDateIntoCache(day) { prayers ->
                        prayerCache[day] = prayers
                    }
                }
                day = day.plusDays(1)
            }
        }
    }

    // Pager state for the prayers list
    val pagerState = rememberPagerState(initialPage = pagingState.todayPage) { pagingState.totalPages }

    // Track current date based on pager state
    val currentDate by remember {
        derivedStateOf { pagingState.pageToDate(pagerState.currentPage) }
    }

    // Load prayers when page changes
    LaunchedEffect(pagerState.currentPage) {
        val pageDate = pagingState.pageToDate(pagerState.currentPage)
        viewModel.loadPrayersForDate(pageDate)

        // Preload adjacent pages
        val prevDate = pagingState.pageToDate((pagerState.currentPage - 1).coerceAtLeast(0))
        val nextPageDate = pagingState.pageToDate((pagerState.currentPage + 1).coerceAtMost(pagingState.totalPages - 1))

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

    // Get today's data for MainWidget
    val todayDate = LocalDate.now()
    val todayPrayerTimes = prayerTimesCache[todayDate] ?: viewModel.getPrayerTimesForDate(todayDate)
    val todayPrayers = prayerCache[todayDate] ?: emptyList()
    val todayPrayerStatusMap = todayPrayers.associateBy { it.prayerName }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // MainWidget - always at top, showing today's info
            MainWidget(
                prayerNames = prayerNames,
                prayerTimes = todayPrayerTimes,
                prayerStatusMap = todayPrayerStatusMap,
                currentDate = todayDate,
                onStatusChange = { prayerName, newStatus, timePrayed ->
                    viewModel.updatePrayerStatusWithTime(
                        prayerName = prayerName,
                        date = todayDate,
                        newStatus = newStatus,
                        timePrayed = timePrayed
                    )
                    updatePrayerCache(prayerCache, todayDate, prayerName, newStatus, timePrayed)
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Date Navigation Row - clicking date opens calendar
            DateNavigationRow(
                currentDate = currentDate,
                onPreviousDate = {
                    coroutineScope.launch {
                        val targetPage = (pagerState.currentPage - 1).coerceAtLeast(0)
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                onNextDate = {
                    coroutineScope.launch {
                        val targetPage = (pagerState.currentPage + 1).coerceAtMost(pagingState.todayPage)
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                onDateClick = {
                    currentMonth = YearMonth.from(currentDate)
                    calendarVisible = true
                }
            )

            // Horizontal pager for just the PrayersList
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
                beyondViewportPageCount = 1
            ) { page ->
                val pageDate = pagingState.pageToDate(page)
                val showPrayerTimes = page == pagingState.todayPage
                val pagePrayerTimes = if (showPrayerTimes) {
                    prayerTimesCache[pageDate] ?: viewModel.getPrayerTimesForDate(pageDate)
                } else {
                    emptyMap()
                }
                val pagePrayers = prayerCache[pageDate] ?: emptyList()
                val prayerStatusMap = pagePrayers.associateBy { it.prayerName }

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
                        updatePrayerCache(prayerCache, pageDate, prayerName, newStatus, null)
                    },
                    onStatusChangeWithTime = { prayerName, newStatus, timePrayed ->
                        viewModel.updatePrayerStatusWithTime(
                            prayerName = prayerName,
                            date = pageDate,
                            newStatus = newStatus,
                            timePrayed = timePrayed
                        )
                        updatePrayerCache(prayerCache, pageDate, prayerName, newStatus, timePrayed)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Calendar bottom sheet
        CalendarBottomSheet(
            showSheet = calendarVisible,
            sheetState = calendarSheetState,
            currentMonth = currentMonth,
            selectedDate = currentDate,
            prayerDataByDate = prayerCache.toMap(),
            onDateSelected = { selectedDate ->
                val targetPage = pagingState.dateToPage(selectedDate)
                coroutineScope.launch {
                    pagerState.scrollToPage(targetPage)
                }
                currentMonth = YearMonth.from(selectedDate)
                calendarVisible = false
            },
            onMonthChanged = { newMonth -> currentMonth = newMonth },
            onDismiss = { calendarVisible = false }
        )
    }
}

private fun updatePrayerCache(
    prayerCache: MutableMap<LocalDate, List<PrayerData>>,
    pageDate: LocalDate,
    prayerName: String,
    newStatus: PrayerStatus,
    timePrayed: LocalTime?
) {
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
}
