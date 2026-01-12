package com.example.awwal.presentation.ui.common.calendar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import java.time.LocalDate
import java.time.YearMonth

/**
 * A calendar overlay that slides down from the top when opened.
 * Tapping outside the calendar dismisses it.
 */
@Composable
fun CalendarOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    prayerDataByDate: Map<LocalDate, List<PrayerData>>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate backdrop opacity
    val backdropAlpha by animateFloatAsState(
        targetValue = if (visible) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "backdropAlpha"
    )

    // Animate calendar offset (slide down from top)
    val calendarOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else (-500).dp,
        animationSpec = tween(durationMillis = 300),
        label = "calendarOffset"
    )

    if (visible || calendarOffsetY > (-500).dp) {
        Box(modifier = modifier.fillMaxSize()) {
            // Backdrop - tapping dismisses the calendar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backdropAlpha))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onDismiss()
                    }
            )

            // Calendar panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = calendarOffsetY)
                    .shadow(16.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Consume clicks on the calendar panel
                    }
                    .padding(16.dp)
            ) {
                PrayerCalendar(
                    initialMonth = currentMonth,
                    selectedDate = selectedDate,
                    prayerDataByDate = prayerDataByDate,
                    onDateSelected = { date ->
                        onDateSelected(date)
                        onDismiss()
                    },
                    onMonthChanged = onMonthChanged
                )
            }

            // Pull handle at bottom of calendar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = calendarOffsetY + 420.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

