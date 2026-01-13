package com.example.awwal.presentation.ui.common.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import java.time.LocalDate
import java.time.YearMonth

/**
 * A calendar displayed in a modal bottom sheet.
 * Similar pattern to PrayerStatusSelector.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarBottomSheet(
    showSheet: Boolean,
    sheetState: SheetState,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    prayerDataByDate: Map<LocalDate, List<PrayerData>>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onDismiss: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

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
        }
    }
}

