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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun PrayersList(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    onStatusChange: (prayerName: String, newStatus: PrayerStatus) -> Unit,
    onStatusChangeWithTime: ((prayerName: String, newStatus: PrayerStatus, timePrayed: LocalTime?) -> Unit)? = null,
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null
) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (headerContent != null) {
            item { headerContent() }
        }
        items(prayerNames) { prayerName ->
            val prayerData = prayerStatusMap[prayerName]
            val startTime = prayerTimes[prayerName]?.let {
                try { LocalTime.parse(it, formatter) } catch (_: Exception) { null }
            }
            val endTime = when (prayerName) {
                "Fajr" -> prayerTimes["Sunrise"]?.let {
                    try { LocalTime.parse(it, formatter) } catch (_: Exception) { null }
                }
                "Isha" -> prayerTimes["Fajr"]?.let {
                    try { LocalTime.parse(it, formatter) } catch (_: Exception) { null }
                }
                else -> {
                    val idx = prayerNames.indexOf(prayerName)
                    val nextName = prayerNames.getOrNull(idx + 1)
                    nextName?.let {
                        prayerTimes[it]?.let { t ->
                            try { LocalTime.parse(t, formatter) } catch (_: Exception) { null }
                        }
                    }
                }
            }
            PrayerItem(
                prayerName = prayerName,
                prayerTime = prayerTimes[prayerName] ?: "",
                currentStatus = prayerData?.prayerStatus ?: PrayerStatus.EMPTY,
                timePrayed = prayerData?.timePrayed,
                onStatusChange = { newStatus ->
                    onStatusChange(prayerName, newStatus)
                },
                onStatusChangeWithTime = if (onStatusChangeWithTime != null) {
                    { newStatus, time ->
                        onStatusChangeWithTime(prayerName, newStatus, time)
                    }
                } else null,
                prayerStartTime = startTime,
                prayerEndTime = endTime
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
