package com.example.awwal.presentation.ui.screens.home.components.widgets.prayersList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A row displaying the current date with navigation arrows.
 * Shows "Today" when the date is today, otherwise shows formatted date.
 * Right arrow is hidden when on today (can't navigate to future).
 */
@Composable
fun DateNavigationRow(
    currentDate: LocalDate,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val isToday = currentDate == today

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
    val displayText = if (isToday) "Today" else currentDate.format(dateFormatter)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left arrow - always visible
        IconButton(
            onClick = onPreviousDate,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous day",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Date text - clickable to open calendar
        Text(
            text = displayText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDateClick()
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Right arrow - hidden when on today (can't go to future)
        Box(modifier = Modifier.size(40.dp)) {
            if (!isToday) {
                IconButton(
                    onClick = onNextDate,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next day",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

