package com.example.awwal.presentation.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.common.DateNavigator
import com.example.awwal.presentation.ui.common.PrayerStatusCard
import com.example.awwal.presentation.viewmodel.PrayersViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

data class DailyPrayerData(
    val date: LocalDate,
    val fajr: PrayerStatus,
    val dhuhr: PrayerStatus,
    val asr: PrayerStatus,
    val maghrib: PrayerStatus,
    val isha: PrayerStatus
)

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayersViewModel = koinViewModel() // Shared ViewModel - super simple!
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val prayers by viewModel.prayersForDate.collectAsState()

    // Load prayers when date changes
    LaunchedEffect(currentDate) {
        viewModel.loadPrayersForDate(currentDate)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DateNavigator(
            currentDate = currentDate,
            onPreviousDay = { currentDate = currentDate.minusDays(1) },
            onNextDay = { currentDate = currentDate.plusDays(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Convert database prayers to display format
            val displayData = prayers.toDailyPrayerData(currentDate)

            PrayerStatusCard(
                prayerName = "Fajr",
                status = displayData.fajr,
                onClick = {
                    viewModel.updatePrayerStatus("Fajr", currentDate, PrayerStatus.PRAYED)
                }
            )
            PrayerStatusCard(
                prayerName = "Dhuhr",
                status = displayData.dhuhr,
                onClick = {
                    viewModel.updatePrayerStatus("Dhuhr", currentDate, PrayerStatus.PRAYED)
                }
            )
            PrayerStatusCard(
                prayerName = "Asr",
                status = displayData.asr,
                onClick = {
                    viewModel.updatePrayerStatus("Asr", currentDate, PrayerStatus.PRAYED)
                }
            )
            PrayerStatusCard(
                prayerName = "Maghrib",
                status = displayData.maghrib,
                onClick = {
                    viewModel.updatePrayerStatus("Maghrib", currentDate, PrayerStatus.PRAYED)
                }
            )
            PrayerStatusCard(
                prayerName = "Isha",
                status = displayData.isha,
                onClick = {
                    viewModel.updatePrayerStatus("Isha", currentDate, PrayerStatus.PRAYED)
                }
            )
        }
    }
}


// Extension function to convert List<PrayerData> to DailyPrayerData
fun List<com.example.awwal.domain.classes.PrayerData>.toDailyPrayerData(date: LocalDate): DailyPrayerData {
    val prayerMap = this.associateBy { it.prayerName }
    return DailyPrayerData(
        date = date,
        fajr = prayerMap["Fajr"]?.prayerStatus ?: PrayerStatus.EMPTY,
        dhuhr = prayerMap["Dhuhr"]?.prayerStatus ?: PrayerStatus.EMPTY,
        asr = prayerMap["Asr"]?.prayerStatus ?: PrayerStatus.EMPTY,
        maghrib = prayerMap["Maghrib"]?.prayerStatus ?: PrayerStatus.EMPTY,
        isha = prayerMap["Isha"]?.prayerStatus ?: PrayerStatus.EMPTY
    )
}
