package com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.screens.home.components.widgets.mainWidget.MainWidgetUiState
import java.time.format.DateTimeFormatter

@Composable
fun PrayerStatusSection(
    state: MainWidgetUiState,
    onMarkPrayerClick: () -> Unit
) {
    if (!state.hasPrayed) {
        Button(
            onClick = onMarkPrayerClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("Mark Prayer Status", fontWeight = FontWeight.SemiBold)
        }
    }
}