package com.example.awwal.presentation.ui.common.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Data class representing a day in the calendar.
 */
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean,
    val prayerStatuses: List<PrayerStatus>
)

/**
 * A calendar composable with smooth month swiping and prayer status rings.
 */
@Composable
fun PrayerCalendar(
    initialMonth: YearMonth,
    selectedDate: LocalDate,
    prayerDataByDate: Map<LocalDate, List<PrayerData>>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

    // Pager for smooth month swiping
    val totalMonthPages = 1200
    val centerPage = totalMonthPages / 2

    fun pageToMonth(page: Int): YearMonth {
        val offset = page - centerPage
        return YearMonth.now().plusMonths(offset.toLong())
    }

    fun monthToPage(month: YearMonth): Int {
        return centerPage + java.time.Period.between(YearMonth.now().atDay(1), month.atDay(1)).toTotalMonths().toInt()
    }

    val pagerState = rememberPagerState(initialPage = monthToPage(initialMonth)) { totalMonthPages }
    val coroutineScope = rememberCoroutineScope()

    // Update parent when page changes
    LaunchedEffect(pagerState.currentPage) {
        val newMonth = pageToMonth(pagerState.currentPage)
        onMonthChanged(newMonth)
    }

    // Prevent swiping to future months
    LaunchedEffect(pagerState.currentPage) {
        val pageMonth = pageToMonth(pagerState.currentPage)
        if (pageMonth.isAfter(YearMonth.now())) {
            pagerState.animateScrollToPage(centerPage)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month header
        MonthHeader(
            currentMonth = pageToMonth(pagerState.currentPage),
            onPreviousMonth = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextMonth = {
                val nextMonth = pageToMonth(pagerState.currentPage + 1)
                if (!nextMonth.isAfter(YearMonth.now())) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            canGoNext = !pageToMonth(pagerState.currentPage + 1).isAfter(YearMonth.now())
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day of week headers
        DayOfWeekHeaders()

        Spacer(modifier = Modifier.height(8.dp))

        // Month pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) { page ->
            val pageMonth = pageToMonth(page)
            val isFutureMonth = pageMonth.isAfter(YearMonth.now())

            if (!isFutureMonth) {
                val calendarDays = remember(pageMonth, prayerDataByDate) {
                    generateCalendarDays(pageMonth, today, prayerDataByDate, prayerNames)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    userScrollEnabled = false
                ) {
                    items(calendarDays) { day ->
                        CalendarDayCell(
                            day = day,
                            isSelected = day.date == selectedDate,
                            onDateSelected = {
                                if (!day.isFuture && day.isCurrentMonth) {
                                    onDateSelected(day.date)
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        CalendarLegend()
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    canGoNext: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.semantics { contentDescription = "Previous month" }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
            onClick = onNextMonth,
            enabled = canGoNext,
            modifier = Modifier.semantics { contentDescription = "Next month" }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (canGoNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun DayOfWeekHeaders() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )
        daysOfWeek.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onDateSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = !day.isFuture && day.isCurrentMonth,
                onClick = onDateSelected
            )
            .semantics {
                contentDescription = "Day ${day.date.dayOfMonth}${if (day.isToday) ", today" else ""}${if (day.isFuture) ", future" else ""}"
            },
        contentAlignment = Alignment.Center
    ) {
        if (day.isCurrentMonth) {
            PrayerStatusRing(
                dayNumber = day.date.dayOfMonth,
                prayerStatuses = day.prayerStatuses,
                isToday = day.isToday,
                isFuture = day.isFuture,
                isSelected = isSelected,
                size = 38.dp,
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF4CAF50), label = "Jamaah")
        LegendItem(color = Color(0xFFFFC107), label = "Prayed")
        LegendItem(color = Color(0xFFFF9800), label = "Late")
        LegendItem(color = Color(0xFFF44336), label = "Missed")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, shape = RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun generateCalendarDays(
    yearMonth: YearMonth,
    today: LocalDate,
    prayerDataByDate: Map<LocalDate, List<PrayerData>>,
    prayerNames: List<String>
): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val daysFromPreviousMonth = (firstDayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    // Previous month padding
    val previousMonth = yearMonth.minusMonths(1)
    val lastDayOfPreviousMonth = previousMonth.atEndOfMonth()
    for (i in daysFromPreviousMonth downTo 1) {
        val date = lastDayOfPreviousMonth.minusDays((i - 1).toLong())
        days.add(CalendarDay(date, false, date == today, date.isAfter(today), getPrayerStatusesForDate(date, prayerDataByDate, prayerNames)))
    }

    // Current month
    for (dayOfMonth in 1..lastDayOfMonth.dayOfMonth) {
        val date = yearMonth.atDay(dayOfMonth)
        days.add(CalendarDay(date, true, date == today, date.isAfter(today), getPrayerStatusesForDate(date, prayerDataByDate, prayerNames)))
    }

    // Next month padding
    val remainingDays = (7 - (days.size % 7)) % 7
    for (i in 1..remainingDays) {
        val date = lastDayOfMonth.plusDays(i.toLong())
        days.add(CalendarDay(date, false, date == today, date.isAfter(today), getPrayerStatusesForDate(date, prayerDataByDate, prayerNames)))
    }

    return days
}

private fun getPrayerStatusesForDate(
    date: LocalDate,
    prayerDataByDate: Map<LocalDate, List<PrayerData>>,
    prayerNames: List<String>
): List<PrayerStatus> {
    val prayerDataList = prayerDataByDate[date] ?: emptyList()
    val statusMap = prayerDataList.associateBy { it.prayerName }
    return prayerNames.map { statusMap[it]?.prayerStatus ?: PrayerStatus.EMPTY }
}

