package com.example.awwal.presentation.ui.common.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.enums.PrayerStatus

/**
 * A circular ring that displays prayer status for a day.
 * The ring is divided into 5 segments (one per prayer: Fajr, Dhuhr, Asr, Maghrib, Isha).
 * Each segment's color represents the status of that prayer.
 */
@Composable
fun PrayerStatusRing(
    dayNumber: Int,
    prayerStatuses: List<PrayerStatus>,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    strokeWidth: Dp = 3.dp,
    isToday: Boolean = false,
    isFuture: Boolean = false,
    isSelected: Boolean = false
) {
    val segmentAngle = 360f / 5
    val gapAngle = 6f
    val effectiveSegmentAngle = segmentAngle - gapAngle

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400),
        label = "ringProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2

            prayerStatuses.forEachIndexed { index, status ->
                val startAngle = -90f + (index * segmentAngle) + (gapAngle / 2)
                val sweepAngle = effectiveSegmentAngle * animatedProgress

                val color = if (isFuture) {
                    Color.Gray.copy(alpha = 0.2f)
                } else {
                    getStatusColor(status)
                }

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    topLeft = androidx.compose.ui.geometry.Offset(
                        (size.toPx() - radius * 2) / 2,
                        (size.toPx() - radius * 2) / 2
                    )
                )
            }

            // Draw today indicator as an inner ring
            if (isToday) {
                drawCircle(
                    color = Color(0xFF4CAF50),
                    radius = radius - strokeWidthPx - 2.dp.toPx(),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Draw selection indicator
            if (isSelected && !isToday) {
                drawCircle(
                    color = Color(0xFF2196F3),
                    radius = radius - strokeWidthPx - 2.dp.toPx(),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        Text(
            text = dayNumber.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = when {
                isToday -> FontWeight.ExtraBold
                isSelected -> FontWeight.Bold
                else -> FontWeight.Normal
            },
            color = when {
                isToday -> Color(0xFF4CAF50)
                isSelected -> Color(0xFF2196F3)
                isFuture -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

fun getStatusColor(status: PrayerStatus): Color {
    return when (status) {
        PrayerStatus.PRAYED -> Color(0xFFFFC107) // Yellow
        PrayerStatus.JAMAAH -> Color(0xFF4CAF50) // Green
        PrayerStatus.LATE -> Color(0xFFFF9800) // Orange
        PrayerStatus.MISSED -> Color(0xFFF44336) // Red
        PrayerStatus.EMPTY -> Color(0xFFE0E0E0) // Light gray
    }
}

