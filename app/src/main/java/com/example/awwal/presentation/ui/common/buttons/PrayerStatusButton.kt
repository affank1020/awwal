package com.example.awwal.presentation.ui.common.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus
import com.example.awwal.presentation.ui.screens.getPrayerColors
import com.example.awwal.presentation.ui.screens.getPrayerIcons
import com.example.awwal.presentation.ui.screens.getPrayerLabels

@Composable
fun PrayerStatusButton(
    status: PrayerStatus,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = getPrayerIcons(status)
    val color: Color = getPrayerColors(status)
    val label: String = getPrayerLabels(status)

    val buttonColor = if (isSelected) color.copy(alpha = 0.5f) else Color.Transparent
    val borderColor = color
    val buttonSize = 72.dp
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        border = BorderStroke(2.dp, borderColor),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = buttonColor,
            contentColor = borderColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = label)
                Spacer(Modifier.height(4.dp))
                Text(
                    label,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
