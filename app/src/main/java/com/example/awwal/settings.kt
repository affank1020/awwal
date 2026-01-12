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

fun getSkyBackground(prayerName: String): Brush {
    return when (prayerName) {
        "Fajr" -> Brush.verticalGradient(listOf(Color(0xFF011318), Color(0xFF01426C)), endY = 900f)
        "Dhuhr" -> Brush.verticalGradient(listOf(Color(0xFF71E0F8), Color(0xFFFFF0A0)), endY = 450f)
        "Asr" -> Brush.verticalGradient(listOf(Color(0xFFAFD3F3), Color(0xFFFFE96E)), endY = 400f)
        "Maghrib" -> Brush.verticalGradient(listOf(Color(0xFFD53000), Color(0xFFF39211)), endY = 200f)
        else -> Brush.verticalGradient(listOf(Color(0xFF020B23), Color(0xFF1A2154)))
    }
}

fun getForegroundColor(prayerName: String): Color {
    return when (prayerName) {
        "Fajr" -> Color(0xFFE3F2FD)
        "Dhuhr" -> Color(0xFF003B72)
        "Asr" -> Color(0xFF935100)
        "Maghrib" -> Color(0xFFFFF4E7)
        "Isha" -> Color(0xFFB39DDB)
        else -> Color.White
    }
}
