package com.example.awwal.presentation.ui.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun rememberDateInMillis(
    date: LocalDate
): Long {
    return remember {
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}