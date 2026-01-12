package com.example.awwal.presentation.ui.screens.home.components.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.buttons.PrayerStatusButton
import com.example.awwal.presentation.ui.common.dialogs.timePicker.TimePickerDialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerStatusSelector(
    prayerName: String,
    currentStatus: PrayerStatus,
    onStatusChange: (PrayerStatus) -> Unit,
    onStatusChangeWithTime: ((PrayerStatus, LocalTime?) -> Unit)? = null,
    showSheet: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    prayerStartTime: LocalTime? = null,
    prayerEndTime: LocalTime? = null
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingStatus by remember { mutableStateOf<PrayerStatus?>(null) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(24.dp))
                val statuses = listOf(
                    PrayerStatus.JAMAAH,
                    PrayerStatus.PRAYED,
                    PrayerStatus.LATE,
                    PrayerStatus.MISSED
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    statuses.forEach { status ->
                        PrayerStatusButton(
                            status = status,
                            isSelected = currentStatus == status,
                            onClick = {
                                if (currentStatus == status) {
                                    // Deselect - clear status
                                    onStatusChangeWithTime?.invoke(PrayerStatus.EMPTY, null)
                                        ?: onStatusChange(PrayerStatus.EMPTY)
                                    onDismiss()
                                } else if (status == PrayerStatus.PRAYED && onStatusChangeWithTime != null) {
                                    // Show time picker for PRAYED status
                                    pendingStatus = status
                                    showTimePicker = true
                                } else {
                                    // For other statuses, just update without time
                                    onStatusChangeWithTime?.invoke(status, null)
                                        ?: onStatusChange(status)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showTimePicker && pendingStatus != null) {
        TimePickerDialog(
            initialTime = prayerStartTime ?: LocalTime.now(),
            minTime = prayerStartTime,
            maxTime = prayerEndTime,
            prayerName = prayerName,
            onTimeSelected = { selectedTime ->
                onStatusChangeWithTime?.invoke(pendingStatus!!, selectedTime)
                    ?: onStatusChange(pendingStatus!!)
                showTimePicker = false
                pendingStatus = null
                onDismiss()
            },
            onDismiss = {
                showTimePicker = false
                pendingStatus = null
            }
        )
    }
}
