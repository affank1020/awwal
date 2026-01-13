package com.example.awwal.presentation.ui.screens.home.components.prayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.getPrayerColors
import com.example.awwal.getPrayerIcons
import com.example.awwal.getPrayerLabels
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerItem(
    modifier: Modifier = Modifier,
    prayerName: String,
    prayerTime: String = "",
    currentStatus: PrayerStatus = PrayerStatus.EMPTY,
    timePrayed: LocalTime? = null,
    onStatusChange: (PrayerStatus) -> Unit = {},
    onStatusChangeWithTime: ((PrayerStatus, LocalTime?) -> Unit)? = null,
    prayerStartTime: LocalTime? = null,
    prayerEndTime: LocalTime? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    val statusColor: Color = getPrayerColors(currentStatus)
    val statusIcon: ImageVector = getPrayerIcons(currentStatus)
    val statusLabel: String = getPrayerLabels(currentStatus)

    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Determine what time text to show
    val timeDisplayText = when {
        currentStatus == PrayerStatus.PRAYED && timePrayed != null -> {
            "Prayed at ${timePrayed.format(formatter)}"
        }
        currentStatus == PrayerStatus.JAMAAH -> {
            "Prayed in Jamaah"
        }
        currentStatus == PrayerStatus.LATE -> {
            "Prayed late"
        }
        currentStatus == PrayerStatus.MISSED -> {
            "Missed"
        }
        prayerTime.isNotBlank() -> {
            "Start time: $prayerTime"
        }
        else -> ""
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (currentStatus != PrayerStatus.EMPTY) statusColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prayer Name and Time
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                if (timeDisplayText.isNotBlank()) {
                    Text(
                        text = timeDisplayText,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentStatus != PrayerStatus.EMPTY && currentStatus != PrayerStatus.MISSED) {
                            statusColor
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = { showSheet = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusLabel,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    PrayerStatusSelector(
        prayerName = prayerName,
        currentStatus = currentStatus,
        onStatusChange = onStatusChange,
        onStatusChangeWithTime = onStatusChangeWithTime,
        showSheet = showSheet,
        sheetState = sheetState,
        onDismiss = {
            coroutineScope.launch {
                showSheet = false
            }
        },
        prayerStartTime = prayerStartTime,
        prayerEndTime = prayerEndTime
    )
}
