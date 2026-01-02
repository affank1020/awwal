package com.example.awwal.presentation.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
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

@Composable
fun PrayerItem(
    modifier: Modifier = Modifier,
    prayerName: String,
    prayerTime: String = "-- : --",
    currentStatus: PrayerStatus = PrayerStatus.EMPTY,
    onStatusChange: (PrayerStatus) -> Unit = {}
) {
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

            // Status Icon (clickable)
            IconButton(
                onClick = cycleToNextStatus
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
}
