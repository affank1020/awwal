package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.ScreenContainer
import com.example.awwal.presentation.ui.common.calendar.CalendarBottomSheet
import com.example.awwal.presentation.ui.screens.home.components.widgets.dailyHadith.DailyHadithWidget
import com.example.awwal.presentation.ui.screens.home.components.widgets.missedPrayers.MissedPrayersWidget
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.MainWidget
import com.example.awwal.presentation.ui.screens.home.components.widgets.prayersList.PrayersWidget
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel()
) {
    // Shared caches for prayer data - needed for calendar and MainWidget
    val prayerCache = remember { mutableStateMapOf<LocalDate, List<PrayerData>>() }
    val prayerTimesCache = remember { mutableStateMapOf<LocalDate, Map<String, String>>() }

    // Observe ViewModel state and sync to caches
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

    // Calendar state
    var calendarVisible by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDateForCalendar by remember { mutableStateOf(LocalDate.now()) }
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

    // Today's data for MainWidget
    val todayDate = LocalDate.now()
    val prayerNames = remember { listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha") }
    val todayPrayerTimes = prayerTimesCache[todayDate] ?: viewModel.getPrayerTimesForDate(todayDate)
    val todayPrayers = prayerCache[todayDate] ?: emptyList()
    val todayPrayerStatusMap = todayPrayers.associateBy { it.prayerName }

    ScreenContainer(modifier) {
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

            // Row of widgets: Missed Prayers and Daily Hadith
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Missed Prayers Widget (1 part)
                MissedPrayersWidget(
                    modifier = Modifier.weight(1f),
                    missedCount = 0, // TODO: Connect to actual data
                    onClick = { /* TODO: Navigate to missed prayers page */ }
                )

                // Daily Hadith Widget (3 parts)
                DailyHadithWidget(
                    modifier = Modifier.weight(3f),
                    hadithText = "The best among you are those who have the best manners and character.",
                    hadithSource = "Sahih Bukhari",
                    onClick = { /* TODO: Expand or share hadith */ }
                )
            }

            // Prayers Widget - self-contained with its own pager and date navigation
            PrayersWidget(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                viewModel = viewModel,
                onDateClick = { date, month ->
                    selectedDateForCalendar = date
                    currentMonth = month
                    calendarVisible = true
                },
                prayerCache = prayerCache,
                prayerTimesCache = prayerTimesCache
            )
        }

        // Calendar bottom sheet
        CalendarBottomSheet(
            showSheet = calendarVisible,
            sheetState = calendarSheetState,
            currentMonth = currentMonth,
            selectedDate = selectedDateForCalendar,
            prayerDataByDate = prayerCache.toMap(),
            onDateSelected = { selectedDate ->
                selectedDateForCalendar = selectedDate
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
