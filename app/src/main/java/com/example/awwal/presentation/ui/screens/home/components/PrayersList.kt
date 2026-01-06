package com.example.awwal.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.PrayerData
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.screens.home.components.prayer.PrayerItem

@Composable
fun PrayersList(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    onStatusChange: (prayerName: String, newStatus: PrayerStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(prayerNames) { prayerName ->
            PrayerItem(
                prayerName = prayerName,
                prayerTime = prayerTimes[prayerName] ?: "-- : --",
                currentStatus = prayerStatusMap[prayerName]?.prayerStatus ?: PrayerStatus.EMPTY,
                onStatusChange = { newStatus ->
                    onStatusChange(prayerName, newStatus)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

