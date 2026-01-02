package com.example.awwal.presentation.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.date.DateNavigator
import com.example.awwal.presentation.ui.screens.home.components.prayerItem.PrayerItem
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

private enum class DateSlideDirection { LEFT, RIGHT }

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel()
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var lastDate by remember { mutableStateOf(currentDate) }
    var slideDirection by remember { mutableStateOf(DateSlideDirection.LEFT) }
    val prayersFromDb by viewModel.prayersForDate.collectAsState()

    // Load prayers when date changes
    LaunchedEffect(currentDate) {
        viewModel.loadPrayersForDate(currentDate)
    }

    // TODO: Replace with actual prayer times from API
    val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
    val prayerTimes = mapOf(
        "Fajr" to "05:30 AM",
        "Dhuhr" to "12:15 PM",
        "Asr" to "03:45 PM",
        "Maghrib" to "06:20 PM",
        "Isha" to "08:00 PM"
    )

    // Create prayer map for quick lookup
    val prayerStatusMap = prayersFromDb.associateBy { it.prayerName }

    // Determine animation direction
    LaunchedEffect(currentDate) {
        slideDirection = when {
            currentDate.isAfter(lastDate) -> DateSlideDirection.LEFT
            currentDate.isBefore(lastDate) -> DateSlideDirection.RIGHT
            else -> DateSlideDirection.LEFT
        }
        lastDate = currentDate
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
            .pointerInput(currentDate) {
                detectDragGestures(
                    onDragEnd = {
                        // handled in dragAmount logic
                    },
                    onDrag = { change, dragAmount ->
                        // handled in drag logic below
                    },
                    onDragStart = {},
                    onDragCancel = {}
                )
            }
    ) {
        // Custom drag logic for intuitive swipe
        var accumulatedDrag by remember { mutableStateOf(0f) }
        val swipeThreshold = 80f
        Box(
            Modifier.pointerInput(currentDate) {
                detectDragGestures(
                    onDragStart = {
                        accumulatedDrag = 0f
                    },
                    onDrag = { change, dragAmount ->
                        accumulatedDrag += dragAmount.x
                    },
                    onDragEnd = {
                        if (accumulatedDrag > swipeThreshold) {
                            // Swipe right: go to previous day
                            slideDirection = DateSlideDirection.RIGHT
                            currentDate = currentDate.minusDays(1)
                        } else if (accumulatedDrag < -swipeThreshold) {
                            // Swipe left: go to next day
                            slideDirection = DateSlideDirection.LEFT
                            currentDate = currentDate.plusDays(1)
                        }
                        accumulatedDrag = 0f
                    }
                )
            }
        ) {
            val directionState = rememberUpdatedState(slideDirection)
            AnimatedContent(
                targetState = currentDate,
                transitionSpec = {
                    if (directionState.value == DateSlideDirection.LEFT) {
                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    } else {
                        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    }
                }
            ) { animatedDate ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        DateNavigator(
                            currentDate = animatedDate,
                            onPrevious = {
                                slideDirection = DateSlideDirection.RIGHT
                                currentDate = animatedDate.minusDays(1)
                            },
                            onNext = {
                                slideDirection = DateSlideDirection.LEFT
                                currentDate = animatedDate.plusDays(1)
                            },
                            onDateSelected = { selectedDate ->
                                slideDirection = if (selectedDate.isAfter(animatedDate)) DateSlideDirection.LEFT else DateSlideDirection.RIGHT
                                currentDate = selectedDate
                            },
                        )
                    }

                    items(prayerNames) { prayerName ->
                        PrayerItem(
                            prayerName = prayerName,
                            prayerTime = prayerTimes[prayerName] ?: "-- : --",
                            currentStatus = prayerStatusMap[prayerName]?.prayerStatus
                                ?: PrayerStatus.EMPTY,
                            onStatusChange = { newStatus ->
                                viewModel.updatePrayerStatus(
                                    prayerName = prayerName,
                                    date = animatedDate,
                                    newStatus = newStatus
                                )
                            }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
