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

@Composable
fun PrayersList(
    prayerNames: List<String>,
    prayerTimes: Map<String, String>,
    prayerStatusMap: Map<String, PrayerData>,
    onStatusChange: (prayerName: String, newStatus: PrayerStatus) -> Unit,
    onStatusChangeWithTime: ((prayerName: String, newStatus: PrayerStatus, timePrayed: LocalTime?, isNextDay: Boolean) -> Unit)? = null,
    nextDayFajrTime: LocalTime? = null, // For Isha's next day validation
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null
) {
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
            // Parse start and end times for validation
            val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
            val startTime = prayerTimes[prayerName]?.let { try { java.time.LocalTime.parse(it, formatter) } catch (e: Exception) { null } }
            val endTime = when (prayerName) {
                "Fajr" -> prayerTimes["Sunrise"]?.let { try { java.time.LocalTime.parse(it, formatter) } catch (e: Exception) { null } }
                "Isha" -> null // handled by nextDayFajrTime
                else -> {
                    val idx = prayerNames.indexOf(prayerName)
                    val nextName = prayerNames.getOrNull(idx + 1)
                    nextName?.let { prayerTimes[it]?.let { t -> try { java.time.LocalTime.parse(t, formatter) } catch (e: Exception) { null } } }
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
                    { newStatus, time, isNextDay ->
                        onStatusChangeWithTime(prayerName, newStatus, time, isNextDay)
                    }
                } else null,
                prayerStartTime = startTime,
                prayerEndTime = endTime,
                nextDayFajrTime = if (prayerName.equals("Isha", ignoreCase = true)) nextDayFajrTime else null
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
