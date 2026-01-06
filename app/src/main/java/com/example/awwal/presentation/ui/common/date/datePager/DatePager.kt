package com.example.awwal.presentation.ui.common.date.datePager

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate

@Composable
fun DatePager(
    totalPages: Int,
    todayPage: Int,
    pageToDate: (Int) -> LocalDate,
    dateToPage: (LocalDate) -> Int,
    modifier: Modifier = Modifier,
    content: @Composable (date: LocalDate, page: Int, pagerState: PagerState) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = todayPage) { totalPages }
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val pageDate = pageToDate(page)
        content(pageDate, page, pagerState)
    }
}

