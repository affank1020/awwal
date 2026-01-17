package com.example.awwal.presentation.ui.screens.home.components.widgets.prayersList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.Widget
import com.example.awwal.presentation.ui.common.date.datePager.DatePagingState
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

/**
 * A self-contained widget that displays the prayers list with date navigation.
 * Manages its own pager state and caching internally.
 */
@Composable
fun PrayersWidget(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel,
    onDateClick: (LocalDate, YearMonth) -> Unit,
    prayerCache: MutableMap<LocalDate, List<PrayerData>>,
    prayerTimesCache: MutableMap<LocalDate, Map<String, String>>
) {
    val prayerNames = remember { listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha") }
    val pagingState = remember { DatePagingState() }
    val coroutineScope = rememberCoroutineScope()

    // Pager state for date navigation
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
        preloadAdjacentPages(
            pagerState = pagerState,
            pagingState = pagingState,
            prayerCache = prayerCache,
            prayerTimesCache = prayerTimesCache,
            viewModel = viewModel
        )
    }

    Widget(
        modifier = modifier,
        onClick = { },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section Title
            Text(
                text = "Your Prayers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Date Navigation Row
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
                    onDateClick(currentDate, YearMonth.from(currentDate))
                }
            )

            // Horizontal pager for just the PrayersList
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
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
                        updatePrayerCacheInternal(prayerCache, pageDate, prayerName, newStatus, null)
                    },
                    onStatusChangeWithTime = { prayerName, newStatus, timePrayed ->
                        viewModel.updatePrayerStatusWithTime(
                            prayerName = prayerName,
                            date = pageDate,
                            newStatus = newStatus,
                            timePrayed = timePrayed
                        )
                        updatePrayerCacheInternal(prayerCache, pageDate, prayerName, newStatus, timePrayed)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Preloads prayer data for adjacent pages to ensure smooth swiping.
 */
private fun preloadAdjacentPages(
    pagerState: PagerState,
    pagingState: DatePagingState,
    prayerCache: MutableMap<LocalDate, List<PrayerData>>,
    prayerTimesCache: MutableMap<LocalDate, Map<String, String>>,
    viewModel: PrayersViewModel
) {
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

/**
 * Updates the prayer cache with new status.
 */
private fun updatePrayerCacheInternal(
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

