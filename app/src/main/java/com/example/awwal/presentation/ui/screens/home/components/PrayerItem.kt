package com.example.awwal.presentation.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerItem(
    modifier: Modifier = Modifier,
    prayerName: String,
    prayerTime: String = "-- : --",
    currentStatus: PrayerStatus = PrayerStatus.EMPTY,
    onStatusChange: (PrayerStatus) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Define status properties
    val statusIcon: ImageVector = when (currentStatus) {
        PrayerStatus.EMPTY -> Icons.Default.FavoriteBorder // Empty for not set
        PrayerStatus.PRAYED -> Icons.Default.CheckCircle // Green checkmark
        PrayerStatus.PRAYED_IN_MASJID -> Icons.Default.LocationOn // Blue location pin
        PrayerStatus.LATE -> Icons.Default.Warning // Amber warning
        PrayerStatus.MISSED -> Icons.Default.Close // Red X
    }

    val statusColor: Color = when (currentStatus) {
        PrayerStatus.EMPTY -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        PrayerStatus.PRAYED -> Color(0xFF4CAF50) // Green
        PrayerStatus.PRAYED_IN_MASJID -> Color(0xFF2196F3) // Blue
        PrayerStatus.LATE -> Color(0xFFFFC107) // Amber
        PrayerStatus.MISSED -> Color(0xFFF44336) // Red
    }

    val statusLabel: String = when (currentStatus) {
        PrayerStatus.EMPTY -> "Not Set"
        PrayerStatus.PRAYED -> "Prayed"
        PrayerStatus.PRAYED_IN_MASJID -> "In Masjid"
        PrayerStatus.LATE -> "Late"
        PrayerStatus.MISSED -> "Missed"
    }

    // Cycle to next status on click
    val cycleToNextStatus: () -> Unit = {
        val nextStatus = when (currentStatus) {
            PrayerStatus.EMPTY -> PrayerStatus.PRAYED
            PrayerStatus.PRAYED -> PrayerStatus.PRAYED_IN_MASJID
            PrayerStatus.PRAYED_IN_MASJID -> PrayerStatus.LATE
            PrayerStatus.LATE -> PrayerStatus.MISSED
            PrayerStatus.MISSED -> PrayerStatus.EMPTY
        }
        onStatusChange(nextStatus)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (currentStatus != PrayerStatus.EMPTY) statusColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prayer Name and Time
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = prayerTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Status Icon (opens bottom sheet)
            IconButton(
                onClick = { showSheet = true }
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusLabel,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background // Ensures full tray matches page background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(Modifier.height(16.dp))
                // Prayer name at top
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(24.dp))
                // Status buttons in grid (remove EMPTY)
                val statuses = listOf(
                    PrayerStatus.PRAYED,
                    PrayerStatus.PRAYED_IN_MASJID,
                    PrayerStatus.LATE,
                    PrayerStatus.MISSED
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    statuses.forEach { status ->
                        val icon: ImageVector = when (status) {
                            PrayerStatus.PRAYED -> Icons.Default.CheckCircle
                            PrayerStatus.PRAYED_IN_MASJID -> Icons.Default.LocationOn
                            PrayerStatus.LATE -> Icons.Default.Warning
                            PrayerStatus.MISSED -> Icons.Default.Close
                            else -> Icons.Default.FavoriteBorder // never used
                        }
                        val color: Color = when (status) {
                            PrayerStatus.PRAYED -> Color(0xFF4CAF50)
                            PrayerStatus.PRAYED_IN_MASJID -> Color(0xFF2196F3)
                            PrayerStatus.LATE -> Color(0xFFFFC107)
                            PrayerStatus.MISSED -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.outline
                        }
                        val label: String = when (status) {
                            PrayerStatus.PRAYED -> "Prayed"
                            PrayerStatus.PRAYED_IN_MASJID -> "In Masjid"
                            PrayerStatus.LATE -> "Late"
                            PrayerStatus.MISSED -> "Missed"
                            else -> "Not Set"
                        }
                        val isSelected = currentStatus == status
                        val buttonColor = if (isSelected) color.copy(alpha = 0.5f) else Color.Transparent
                        val borderColor = color
                        OutlinedButton(
                            onClick = {
                                if (isSelected) {
                                    onStatusChange(PrayerStatus.EMPTY)
                                } else {
                                    onStatusChange(status)
                                }
                                coroutineScope.launch { sheetState.hide() }
                                showSheet = false
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(2.dp, borderColor),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = buttonColor,
                                contentColor = borderColor
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(icon, contentDescription = label)
                                Spacer(Modifier.height(4.dp))
                                Text(label)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
