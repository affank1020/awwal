package com.example.awwal.presentation.ui.common.date.dateNavigator

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.awwal.presentation.ui.common.buttons.iconButtons.BackArrowButton
import com.example.awwal.presentation.ui.common.buttons.iconButtons.ForwardArrowButton
import com.example.awwal.presentation.ui.common.date.DateText

@Composable
fun PastControls (
    modifier: Modifier = Modifier,
    currentDate: java.time.LocalDate,
    onPrevious: () -> Unit,
    onNext: (() -> Unit)? = null,
    isNextEnabled: Boolean = true,
    openDialog: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackArrowButton(onPrevious)

        DateText(
            currentDate,
            onClick = openDialog,
            modifier = Modifier
        )

        if (onNext != null && isNextEnabled) {
            ForwardArrowButton(onNext)
        }
    }
}