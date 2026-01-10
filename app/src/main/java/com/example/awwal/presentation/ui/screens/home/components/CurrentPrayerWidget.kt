package com.example.awwal.presentation.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.screens.home.components.prayer.PrayerStatusSelector
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun getSkyBackground(prayerName: String): Brush {
    return when (prayerName) {
        "Fajr" -> Brush.verticalGradient(listOf(Color(0xFF011318), Color(0xFF01426C)), endY = 900f)
        "Dhuhr" -> Brush.verticalGradient(listOf(Color(0xFF71E0F8), Color(0xFFFFF0A0)), endY = 450f)
        "Asr" -> Brush.verticalGradient(listOf(Color(0xFFAFD3F3), Color(0xFFFFE96E)), endY = 400f)
        "Maghrib" -> Brush.verticalGradient(listOf(Color(0xFFD53000), Color(0xFFF39211)), endY = 200f)
        else -> Brush.verticalGradient(listOf(Color(0xFF020B23), Color(0xFF1A2154)))
    }
}

private fun getForegroundColor(prayerName: String): Color {
    return when (prayerName) {
        "Fajr" -> Color(0xFFE3F2FD)
        "Dhuhr" -> Color(0xFF003B72)
        "Asr" -> Color(0xFF935100)
        "Maghrib" -> Color(0xFFFFF4E7)
        "Isha" -> Color(0xFFB39DDB)
        else -> Color.White
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentPrayerWidget(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    modifier: Modifier = Modifier,
    currentDate: LocalDate = LocalDate.now(),
    nextDayFajrTime: LocalTime? = null, // Fajr time for the next day (for Isha end time)
    onStatusChange: ((String, PrayerStatus, LocalTime?, Boolean) -> Unit)? = null // Added isNextDay parameter
) {
    val now = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    // Parse times
    val times = prayerNames.mapNotNull { name ->
        prayerTimes[name]?.let { timeStr ->
            try {
                name to LocalTime.parse(timeStr, formatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    val fajrTime = times.find { it.first == "Fajr" }?.second
    val sunriseTime = prayerTimes["Sunrise"]?.let {
        try { LocalTime.parse(it, formatter) } catch (e: Exception) { null }
    }
    val sunsetTime = prayerTimes["Maghrib"]?.let {
        try { LocalTime.parse(it, formatter) } catch (e: Exception) { null }
    }
    val ishaTime = times.find { it.first == "Isha" }?.second

    // Determine current prayer - handle the edge case of being after midnight but before Fajr
    // In this case, we're still in Isha time from the previous day
    val isBeforeFajr = fajrTime != null && now < fajrTime
    val isAfterIsha = ishaTime != null && now >= ishaTime

    val currentPrayerIndex: Int
    val currentPrayerName: String
    val currentPrayerStartTime: LocalTime?

    if (isBeforeFajr && !isAfterIsha) {
        // After midnight but before Fajr - we're still in Isha from previous day
        currentPrayerName = "Isha"
        currentPrayerStartTime = ishaTime
        currentPrayerIndex = times.indexOfFirst { it.first == "Isha" }
    } else {
        currentPrayerIndex = times.indexOfLast { (_, time) -> now >= time }
        currentPrayerName = times.getOrNull(currentPrayerIndex)?.first ?: "Isha"
        currentPrayerStartTime = times.getOrNull(currentPrayerIndex)?.second
    }

    // Get current prayer data
    val currentPrayerData = prayerStatusMap[currentPrayerName]
    val currentStatus = currentPrayerData?.prayerStatus ?: PrayerStatus.EMPTY
    val timePrayed = currentPrayerData?.timePrayed
    val hasPrayed = currentStatus in listOf(PrayerStatus.PRAYED, PrayerStatus.JAMAAH, PrayerStatus.LATE)

    // Determine next prayer/event
    val nextEventName: String
    val nextEventTime: LocalTime?
    val nextEventLabel: String

    when {
        currentPrayerName == "Fajr" && !hasPrayed -> {
            // Fajr is current and not prayed - show sunrise as deadline
            nextEventName = "Sunrise"
            nextEventTime = sunriseTime
            nextEventLabel = "Sunrise"
        }
        currentPrayerName == "Fajr" && hasPrayed -> {
            // Fajr prayed - show Dhuhr start time
            nextEventName = "Dhuhr"
            nextEventTime = times.find { it.first == "Dhuhr" }?.second
            nextEventLabel = "Dhuhr"
        }
        currentPrayerName == "Isha" -> {
            // Isha is current - show Fajr (next day) as next prayer
            nextEventName = "Fajr"
            nextEventTime = nextDayFajrTime ?: fajrTime // Use next day Fajr if available
            nextEventLabel = "Fajr"
        }
        else -> {
            // For other prayers, show next prayer start time
            val nextPrayerIndex = (currentPrayerIndex + 1).coerceAtMost(times.size - 1)
            val nextPrayer = times.getOrNull(nextPrayerIndex)
            nextEventName = nextPrayer?.first ?: "Fajr"
            nextEventTime = nextPrayer?.second
            nextEventLabel = nextEventName
        }
    }

    // Calculate time until next event
    val timeUntilNext = nextEventTime?.let {
        val duration = if (currentPrayerName == "Isha" && it < now) {
            // Next event is tomorrow (Fajr after midnight)
            val minutesUntilMidnight = Duration.between(now, LocalTime.MAX).toMinutes() + 1
            val minutesAfterMidnight = Duration.between(LocalTime.MIDNIGHT, it).toMinutes()
            Duration.ofMinutes(minutesUntilMidnight + minutesAfterMidnight)
        } else {
            Duration.between(now, it)
        }
        if (!duration.isNegative) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d",
                duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
        } else "--"
    } ?: "--"

    val backgroundBrush = getSkyBackground(currentPrayerName)
    val foregroundColor = getForegroundColor(currentPrayerName)

    // Sheet state for status selector
    val sheetState = rememberModalBottomSheetState()
    var showStatusSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp))
            .background(backgroundBrush, shape = RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date in top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = currentDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = foregroundColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Current prayer section
            if (hasPrayed) {
                // User has prayed - show confirmation
                val prayedAtText = if (timePrayed != null && currentStatus == PrayerStatus.PRAYED) {
                    "Prayed $currentPrayerName at ${timePrayed.format(formatter)}"
                } else {
                    when (currentStatus) {
                        PrayerStatus.JAMAAH -> "Prayed $currentPrayerName in Jamaah"
                        PrayerStatus.LATE -> "Prayed $currentPrayerName late"
                        else -> "You have prayed $currentPrayerName"
                    }
                }
                Text(
                    text = prayedAtText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = foregroundColor,
                    fontWeight = FontWeight.Bold
                )
            } else {
                // User has not prayed - show prompt with action
                Text(
                    text = "Have you prayed $currentPrayerName?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = foregroundColor,
                    fontWeight = FontWeight.Bold
                )

                // Quick action button
                Button(
                    onClick = { showStatusSheet = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = foregroundColor.copy(alpha = 0.2f),
                        contentColor = foregroundColor
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Mark Prayer Status", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Next prayer/event info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$nextEventLabel at ${nextEventTime?.format(formatter) ?: "--"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = foregroundColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "In $timeUntilNext",
                    style = MaterialTheme.typography.bodyMedium,
                    color = foregroundColor.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sunrise and Sunset info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Sunrise",
                        tint = foregroundColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Sunrise ${sunriseTime?.format(formatter) ?: "--"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = foregroundColor.copy(alpha = 0.8f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Sunset",
                        tint = foregroundColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Sunset ${sunsetTime?.format(formatter) ?: "--"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = foregroundColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    // Calculate prayer end time for validation
    val currentPrayerEndTime: LocalTime? = when (currentPrayerName) {
        "Fajr" -> sunriseTime
        "Isha" -> null // handled by nextDayFajrTime
        else -> {
            val nextIdx = times.indexOfFirst { it.first == currentPrayerName } + 1
            times.getOrNull(nextIdx)?.second
        }
    }

    // Prayer status selector sheet
    PrayerStatusSelector(
        prayerName = currentPrayerName,
        currentStatus = currentStatus,
        onStatusChange = { newStatus ->
            onStatusChange?.invoke(currentPrayerName, newStatus, null, false)
        },
        onStatusChangeWithTime = { newStatus, time, isNextDay ->
            onStatusChange?.invoke(currentPrayerName, newStatus, time, isNextDay)
        },
        showSheet = showStatusSheet,
        sheetState = sheetState,
        onDismiss = { showStatusSheet = false },
        prayerStartTime = currentPrayerStartTime,
        prayerEndTime = currentPrayerEndTime,
        nextDayFajrTime = if (currentPrayerName == "Isha") nextDayFajrTime else null
    )
}
