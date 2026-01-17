package com.example.awwal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.awwal.domain.classes.enums.PrayerStatus

fun getPrayerIcons(currentStatus: PrayerStatus): ImageVector {
    return when (currentStatus) {
        PrayerStatus.EMPTY -> Icons.Outlined.AddCircle
        PrayerStatus.PRAYED -> Icons.Outlined.Check
        PrayerStatus.JAMAAH -> Icons.Outlined.Person
        PrayerStatus.LATE -> Icons.Outlined.Warning
        PrayerStatus.MISSED -> Icons.Outlined.Close
    }
}

fun getPrayerColors(currentStatus: PrayerStatus): Color {
    return when (currentStatus) {
        PrayerStatus.EMPTY -> Color(0xFFBDBDBD)
        PrayerStatus.PRAYED -> Color(0xFFFFD049)
        PrayerStatus.JAMAAH -> Color(0xFF4CAF50)
        PrayerStatus.LATE -> Color(0xFFFF8C00)
        PrayerStatus.MISSED -> Color(0xFFF44336)
    }
}

fun getPrayerLabels(currentStatus: PrayerStatus): String {
    return when (currentStatus) {
        PrayerStatus.EMPTY -> "Not Set"
        PrayerStatus.PRAYED -> "Prayed"
        PrayerStatus.JAMAAH -> "In Jamaah"
        PrayerStatus.LATE -> "Late"
        PrayerStatus.MISSED -> "Missed"
    }
}

fun getAccentColor(prayerName: String): Color {
    return when (prayerName.lowercase()) {
        "fajr" -> Color(0xFF7C4DFF)
        "dhuhr" -> Color(0xFF2196F3)
        "asr" -> Color(0xFFFF9800)
        "maghrib" -> Color(0xFFE91E63)
        "isha" -> Color(0xFF3F51B5)
        else -> Color(0xFF7C4DFF)
    }
}
