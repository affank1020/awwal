package com.example.awwal.presentation.ui.common.date

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@Composable
fun DateText(
    currentDate: java.time.LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")),
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Toggle Date Picker"
        )
    }
}