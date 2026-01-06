package com.example.awwal.presentation.ui.common.date.datePager

import java.time.LocalDate

class DatePagingState(
    val totalPages: Int = 10000,
    val today: LocalDate = LocalDate.now()
) {
    val todayPage: Int = totalPages - 1
    fun pageToDate(page: Int): LocalDate = today.minusDays((totalPages - 1 - page).toLong())
    fun dateToPage(date: LocalDate): Int {
        val daysFromToday = (today.toEpochDay() - date.toEpochDay()).toInt().coerceAtLeast(0)
        return (totalPages - 1 - daysFromToday).coerceIn(0, totalPages - 1)
    }
}

