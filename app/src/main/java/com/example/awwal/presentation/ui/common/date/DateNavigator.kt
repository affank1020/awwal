package com.example.awwal.presentation.ui.common.date

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.ui.common.buttons.iconButtons.BackArrowButton
import com.example.awwal.presentation.ui.common.buttons.iconButtons.ForwardArrowButton
import com.example.awwal.presentation.ui.common.utils.rememberDateInMillis
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateNavigator(
    currentDate: LocalDate,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onNext: (() -> Unit)? = null,
    onDateSelected: (LocalDate) -> Unit,
    futureDates: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }

    val todayMillis = rememberDateInMillis(LocalDate.now())
    val currentDateMillis = rememberDateInMillis(currentDate)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                if (futureDates) true else utcTimeMillis <= todayMillis
        }
    )

    LaunchedEffect(currentDate) {
        datePickerState.selectedDateMillis = currentDateMillis
    }

    fun handleDateSelected() {
        datePickerState.selectedDateMillis?.let { millis ->
            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            if (selectedDate != currentDate) {
                onDateSelected(selectedDate)
            }
        }
        showDialog = false
    }

    val nextDate = currentDate.plusDays(1)
    val isNextEnabled = futureDates || !nextDate.isAfter(LocalDate.now())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackArrowButton(onPrevious)

        DateText(currentDate, onClick = { showDialog = true })

        if (onNext != null && isNextEnabled) {
            ForwardArrowButton(onNext)
        }
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { handleDateSelected() }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = null,
                headline = null,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
