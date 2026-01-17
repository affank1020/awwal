package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.getForegroundColor
import com.example.awwal.getSkyBackground
import com.example.awwal.presentation.ui.common.contexts.getPrayerContext
import com.example.awwal.presentation.ui.screens.home.components.prayer.PrayerStatusSelector
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWidget(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    modifier: Modifier = Modifier,
    currentDate: LocalDate = LocalDate.now(),
    onStatusChange: ((String, PrayerStatus, LocalTime?) -> Unit)? = null
) {
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000)
        }
    }
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Get prayer context
    val prayerContext = getPrayerContext(prayerNames, prayerTimes, prayerStatusMap, now, formatter)

    val currentPrayerName = prayerContext.currentPrayerName
    val currentPrayerStartTime = prayerContext.currentPrayerStartTime
    val currentStatus = prayerContext.currentStatus
    val timePrayed = prayerContext.timePrayed
    val hasPrayed = prayerContext.hasPrayed
    val nextEventTime = prayerContext.nextEventTime
    val nextEventLabel = prayerContext.nextEventLabel
    val sunriseTime = prayerContext.sunriseTime
    val sunsetTime = prayerContext.sunsetTime
    val currentPrayerEndTime = prayerContext.currentPrayerEndTime

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
            // Date and time in top right
            DateAndTimeRow(
                date = currentDate,
                now = now,
                foregroundColor = foregroundColor
            )

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
            NextPrayerInfo(
                nextEventLabel = nextEventLabel,
                nextEventTime = nextEventTime,
                now = now,
                currentPrayerName = currentPrayerName,
                foregroundColor = foregroundColor
            )

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

    // Prayer status selector sheet
    PrayerStatusSelector(
        prayerName = currentPrayerName,
        currentStatus = currentStatus,
        onStatusChange = { newStatus ->
            onStatusChange?.invoke(currentPrayerName, newStatus, null)
        },
        onStatusChangeWithTime = { newStatus, time ->
            onStatusChange?.invoke(currentPrayerName, newStatus, time)
        },
        showSheet = showStatusSheet,
        sheetState = sheetState,
        onDismiss = { showStatusSheet = false },
        prayerStartTime = currentPrayerStartTime,
        prayerEndTime = currentPrayerEndTime
    )
}

