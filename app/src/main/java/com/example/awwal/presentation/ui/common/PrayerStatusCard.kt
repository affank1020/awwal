package com.example.awwal.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus

// Extension properties for UI-specific data
val PrayerStatus.label: String
    get() = when (this) {
        PrayerStatus.PRAYED -> "Prayed"
        PrayerStatus.JAMAAH -> "Jamaah"
        PrayerStatus.LATE -> "Late"
        PrayerStatus.MISSED -> "Missed"
        PrayerStatus.EMPTY -> "Not Set"
        else -> {"Note Set"}
    }

val PrayerStatus.color: Color
    get() = when (this) {
        PrayerStatus.PRAYED -> Color(0xFF4CAF50) // Green
        PrayerStatus.JAMAAH -> Color(0xFF2196F3) // Blue
        PrayerStatus.LATE -> Color(0xFFFFC107) // Amber
        PrayerStatus.MISSED -> Color(0xFFF44336) // Red
        PrayerStatus.EMPTY -> Color(0xFF9E9E9E) // Grey
        else -> Color.Gray
    }

@Composable
fun PrayerStatusCard(
    prayerName: String,
    status: PrayerStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prayerName,
                style = MaterialTheme.typography.titleMedium
            )

            StatusBadge(status = status)
        }
    }
}

@Composable
fun StatusBadge(status: PrayerStatus) {
    Box(
        modifier = Modifier
            .background(color = status.color.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.label,
            color = status.color, // Use the solid color for text
            style = MaterialTheme.typography.labelMedium
        )
    }
}
