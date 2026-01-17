package com.example.awwal.presentation.ui.screens.home.components.widgets.dailyHadith

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.common.Widget

@Composable
fun DailyHadithWidget(
    modifier: Modifier = Modifier,
    hadithText: String = "Daily Hadith",
    hadithSource: String = "",
    onClick: () -> Unit = {}
) {
    Widget(
        modifier = modifier,
        onClick = onClick,
        height = 120
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Daily Hadith",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hadithText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (hadithSource.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = hadithSource,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

