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
import com.example.awwal.presentation.ui.screens.getPrayerColors
import com.example.awwal.presentation.ui.screens.getPrayerIcons
import com.example.awwal.presentation.ui.screens.getPrayerLabels
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

    val statusColor: Color = getPrayerColors(currentStatus)
    val statusIcon: ImageVector = getPrayerIcons(currentStatus)
    val statusLabel: String = getPrayerLabels(currentStatus)

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

    PrayerStatusSelector(
        prayerName = prayerName,
        currentStatus = currentStatus,
        onStatusChange = onStatusChange,
        showSheet = showSheet,
        sheetState = sheetState,
        onDismiss = {
            coroutineScope.launch {
                showSheet = false
            }
        }
    )
}
