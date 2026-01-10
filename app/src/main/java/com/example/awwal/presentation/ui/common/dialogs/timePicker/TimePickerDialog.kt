package com.example.awwal.presentation.ui.common.dialogs.timePicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.common.dialogs.Dialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Time picker dialog with validation for prayer time windows.
 * Supports Isha's special case where the window extends past midnight to Fajr next day.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime = LocalTime.now(),
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    prayerName: String = "",
    isIshaWithNextDayOption: Boolean = false,
    nextDayFajrTime: LocalTime? = null,
    onTimeSelected: (LocalTime, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var isNextDay by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // For isha, we may have next day option
    val effectiveMinTime = if (isNextDay) LocalTime.MIDNIGHT else minTime
    val effectiveMaxTime = if (isNextDay) nextDayFajrTime else maxTime

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )

    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    val onConfirm = {
        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

        // Validate time is within bounds
        val isValid = if (isNextDay) {
            // After midnight: must be before Fajr next day
            nextDayFajrTime == null || selectedTime <= nextDayFajrTime
        } else {
            // Same day: must be within prayer window
            val afterMin = effectiveMinTime == null || selectedTime >= effectiveMinTime
            val beforeMax = effectiveMaxTime == null || selectedTime <= effectiveMaxTime
            afterMin && beforeMax
        }

        if (isValid) {
            onTimeSelected(selectedTime, isNextDay)
        } else {
            validationError = if (isNextDay) {
                "Time must be before Fajr (${nextDayFajrTime?.format(formatter)})"
            } else {
                "Time must be within the prayer window"
            }
        }
    }

    Dialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = "When did you pray $prayerName?",
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show day selector for Isha
            if (isIshaWithNextDayOption) {
                Text(
                    text = "Select the day you prayed:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isNextDay,
                        onClick = { isNextDay = false; validationError = null },
                        label = { Text("Same day") }
                    )
                    FilterChip(
                        selected = isNextDay,
                        onClick = { isNextDay = true; validationError = null },
                        label = { Text("After midnight") }
                    )
                }

                if (isNextDay && nextDayFajrTime != null) {
                    Text(
                        text = "Select a time between 12:00 AM and ${nextDayFajrTime.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Show time window info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (effectiveMinTime != null) {
                    Text(
                        text = "Start time: ${effectiveMinTime.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (effectiveMaxTime != null) {
                    Text(
                        text = "End time: ${effectiveMaxTime.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TimePicker(state = timePickerState)

            // Show validation error
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}