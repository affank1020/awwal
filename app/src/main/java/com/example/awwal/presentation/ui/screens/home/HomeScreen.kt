package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.calendar.CalendarOverlay
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.ui.common.date.datePager.DatePager
import com.example.awwal.presentation.ui.screens.home.components.PrayersList
import com.example.awwal.presentation.ui.screens.home.components.mainWidget.MainWidget
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
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
    var calendarVisible by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val coroutineScope = rememberCoroutineScope()

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
        var currentPage by remember { mutableIntStateOf(pagingState.todayPage) }
        var capturedPagerState by remember { mutableStateOf<androidx.compose.foundation.pager.PagerState?>(null) }

        DatePager(
            totalPages = pagingState.totalPages,
            todayPage = pagingState.todayPage,
            pageToDate = { pagingState.pageToDate(it) },
            modifier = Modifier.fillMaxSize()
        ) { pageDate, page, pagerState ->
            LaunchedEffect(pagerState) {
                capturedPagerState = pagerState
            }
            LaunchedEffect(page) {
                currentPage = page
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
                // Calendar button header
                CalendarButtonHeader(
                    onCalendarClick = { calendarVisible = true }
                )

                // Prayer list
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
                    modifier = Modifier.fillMaxSize(),
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
                                    updatePrayerCache(prayerCache, pageDate, prayerName, newStatus, timePrayed)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                )
            }
        }

        // Calendar overlay - placed OUTSIDE DatePager so it overlays entire screen
        CalendarOverlay(
            visible = calendarVisible,
            onDismiss = { calendarVisible = false },
            currentMonth = currentMonth,
            selectedDate = pagingState.pageToDate(currentPage),
            prayerDataByDate = prayerCache.toMap(),
            onDateSelected = { selectedDate ->
                val targetPage = pagingState.dateToPage(selectedDate)
                capturedPagerState?.let { pagerState ->
                    coroutineScope.launch {
                        pagerState.scrollToPage(targetPage)
                    }
                }
                currentMonth = YearMonth.from(selectedDate)
                calendarVisible = false
            },
            onMonthChanged = { newMonth -> currentMonth = newMonth }
        )
    }
}

@Composable
private fun CalendarButtonHeader(
    onCalendarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Calendar button - centered and transparent
        OutlinedButton(
            onClick = onCalendarClick,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Open calendar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Calendar", style = MaterialTheme.typography.labelLarge)
        }
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
