package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.MainWidgetContent
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components.PrayerBanner
import com.example.awwal.presentation.ui.theme.AwwalTheme
import java.time.LocalDate
import java.time.LocalTime

/**
 * Preview parameter provider for all prayer times.
 */
class PrayerTimePreviewProvider : PreviewParameterProvider<MainWidgetUiState> {
    override val values: Sequence<MainWidgetUiState> = sequenceOf(
        MainWidgetUiState.forPrayer("Fajr", LocalTime.of(5, 45)),
        MainWidgetUiState.forPrayer("Dhuhr", LocalTime.of(12, 30)),
        MainWidgetUiState.forPrayer("Asr", LocalTime.of(16, 0)),
        MainWidgetUiState.forPrayer("Maghrib", LocalTime.of(18, 45)),
        MainWidgetUiState.forPrayer("Isha", LocalTime.of(20, 30))
    )
}

@Preview(showBackground = true, name = "All Prayer Times")
@Composable
private fun MainWidgetAllPrayersPreview(
    @PreviewParameter(PrayerTimePreviewProvider::class) state: MainWidgetUiState
) {
    AwwalTheme {
        MainWidgetContent(
            state = state,
            onMarkPrayerClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}