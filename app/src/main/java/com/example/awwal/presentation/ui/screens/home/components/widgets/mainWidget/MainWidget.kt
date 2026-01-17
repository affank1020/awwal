package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.contexts.getPrayerContext
import com.example.awwal.presentation.ui.screens.home.components.prayer.PrayerStatusSelector
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.MainWidgetContent
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
    onStatusChange: ((String, PrayerStatus, LocalTime?) -> Unit)? = null,
    mockNow: LocalTime? = null
) {
    // Time state - uses mock if provided, otherwise live updates
    var now by remember { mutableStateOf(mockNow ?: LocalTime.now()) }
    LaunchedEffect(mockNow) {
        if (mockNow != null) {
            now = mockNow
        } else {
            while (true) {
                now = LocalTime.now()
                delay(1000)
            }
        }
    }

    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val prayerContext = getPrayerContext(prayerNames, prayerTimes, prayerStatusMap, now, formatter)

    // Convert to UI state
    val uiState = MainWidgetUiState(
        currentPrayerName = prayerContext.currentPrayerName,
        currentDate = currentDate,
        currentTime = now,
        hasPrayed = prayerContext.hasPrayed,
        currentStatus = prayerContext.currentStatus,
        timePrayed = prayerContext.timePrayed,
        nextEventLabel = prayerContext.nextEventLabel,
        nextEventTime = prayerContext.nextEventTime,
        sunriseTime = prayerContext.sunriseTime,
        sunsetTime = prayerContext.sunsetTime,
        currentPrayerStartTime = prayerContext.currentPrayerStartTime,
        currentPrayerEndTime = prayerContext.currentPrayerEndTime
    )

    // Sheet state
    val sheetState = rememberModalBottomSheetState()
    var showStatusSheet by remember { mutableStateOf(false) }

    MainWidgetContent(
        state = uiState,
        onMarkPrayerClick = { showStatusSheet = true },
        modifier = modifier
    )

    // Prayer status selector that pulls up from the bottom
    PrayerStatusSelector(
        prayerName = uiState.currentPrayerName,
        currentStatus = uiState.currentStatus,
        onStatusChange = { newStatus ->
            onStatusChange?.invoke(uiState.currentPrayerName, newStatus, null)
        },
        onStatusChangeWithTime = { newStatus, time ->
            onStatusChange?.invoke(uiState.currentPrayerName, newStatus, time)
        },
        showSheet = showStatusSheet,
        sheetState = sheetState,
        onDismiss = { showStatusSheet = false },
        prayerStartTime = uiState.currentPrayerStartTime,
        prayerEndTime = uiState.currentPrayerEndTime
    )
}