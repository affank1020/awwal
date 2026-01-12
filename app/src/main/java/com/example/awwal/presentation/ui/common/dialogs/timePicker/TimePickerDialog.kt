package com.example.awwal.presentation.ui.common.dialogs.timePicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.common.dialogs.Dialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Time picker dialog with validation for prayer time windows.
 * Handles midnight crossing naturally for Isha prayers.
 */
@Composable
fun TimePickerDialog(
    initialTime: LocalTime = LocalTime.now(),
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    prayerName: String = "",
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(minTime ?: initialTime) }

    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    val onConfirm = {
        onTimeSelected(selectedTime)
    }

    Dialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = "When did you pray $prayerName?",
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show time window info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (minTime != null) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = minTime.format(formatter),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (maxTime != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "End",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = maxTime.format(formatter),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Circular time picker
            if (minTime != null && maxTime != null) {
                CircularTimePicker(
                    startTime = minTime,
                    endTime = maxTime,
                    initialTime = minTime, // Always start at the beginning of the window
                    onTimeChanged = { time ->
                        selectedTime = time
                    },
                    modifier = Modifier.padding(16.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    progressColor = MaterialTheme.colorScheme.primary,
                    handleColor = MaterialTheme.colorScheme.primary
                )
            } else {
                // Fallback if times are not available
                Text(
                    text = "Time window not available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Helper text
            Text(
                text = "Drag the handle around the circle to select the time you prayed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
