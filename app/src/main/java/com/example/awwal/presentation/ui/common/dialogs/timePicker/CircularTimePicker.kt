package com.example.awwal.presentation.ui.common.dialogs.timePicker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

/**
 * A circular time picker that allows the user to select a time within a given range
 * by dragging a handle around the edge of a circle.
 *
 * The picker is shaped like a horseshoe with a gap at the top to clearly indicate
 * the start and end of the time window.
 *
 * @param startTime The minimum time (maps to left side of gap)
 * @param endTime The maximum time (maps to right side of gap)
 * @param initialTime The initial selected time
 * @param onTimeChanged Callback when the selected time changes
 * @param modifier Modifier for the composable
 * @param size The diameter of the circle
 * @param strokeWidth The width of the circle outline
 * @param handleRadius The radius of the draggable handle
 * @param trackColor The color of the circle outline
 * @param progressColor The color of the progress arc
 * @param handleColor The color of the draggable handle
 * @param gapAngle The angle of the gap at the top in degrees
 */
@Composable
fun CircularTimePicker(
    startTime: LocalTime,
    endTime: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    initialTime: LocalTime = startTime,
    size: Dp = 240.dp,
    strokeWidth: Dp = 12.dp,
    handleRadius: Dp = 16.dp,
    trackColor: Color = MaterialTheme.colorScheme.onSurface,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    handleColor: Color = MaterialTheme.colorScheme.primary,
    gapAngle: Float = 30f // Gap at the top in degrees
) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Calculate total duration in minutes
    val totalMinutes = calculateDurationMinutes(startTime, endTime)

    // Calculate initial progress (0.0 to 1.0)
    val initialProgress = calculateProgress(startTime, initialTime, totalMinutes)

    var progress by remember { mutableFloatStateOf(initialProgress) }
    var isDragging by remember { mutableStateOf(false) }

    // Animate progress for smooth updates
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = if (isDragging) 0 else 150),
        label = "progress"
    )

    // Calculate current time based on progress
    val currentTime = remember(animatedProgress, startTime, totalMinutes) {
        calculateTimeFromProgress(startTime, animatedProgress, totalMinutes)
    }

    // Notify parent of time changes
    LaunchedEffect(currentTime) {
        onTimeChanged(currentTime)
    }

    // Arc angles for horseshoe shape
    val halfGap = gapAngle / 2
    val arcStartAngle = -90f + halfGap // Start just after the gap (clockwise from top)
    val arcSweepAngle = 360f - gapAngle // Total sweep excluding the gap

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            progress = calculateProgressFromOffset(
                                offset = offset,
                                center = Offset(size.toPx() / 2, size.toPx() / 2),
                                totalMinutes = totalMinutes,
                                gapAngle = gapAngle
                            )
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            progress = calculateProgressFromOffset(
                                offset = change.position,
                                center = Offset(size.toPx() / 2, size.toPx() / 2),
                                totalMinutes = totalMinutes,
                                gapAngle = gapAngle
                            )
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                        onDragCancel = {
                            isDragging = false
                        }
                    )
                }
        ) {
            val canvasSize = size.toPx()
            val center = Offset(canvasSize / 2, canvasSize / 2)
            val radius = (canvasSize - strokeWidth.toPx() - handleRadius.toPx()) / 2
            val strokeWidthPx = strokeWidth.toPx()
            val handleRadiusPx = handleRadius.toPx()

            // Draw background track (horseshoe shape)
            drawArc(
                color = trackColor,
                startAngle = arcStartAngle,
                sweepAngle = arcSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Draw progress arc
            val progressSweepAngle = animatedProgress * arcSweepAngle
            drawArc(
                color = progressColor,
                startAngle = arcStartAngle,
                sweepAngle = progressSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Calculate handle position
            // Map progress to the arc (starting from arcStartAngle)
            val handleAngleDegrees = arcStartAngle + (animatedProgress * arcSweepAngle)
            val handleAngleRadians = Math.toRadians(handleAngleDegrees.toDouble())
            val handleX = center.x + radius * cos(handleAngleRadians).toFloat()
            val handleY = center.y + radius * sin(handleAngleRadians).toFloat()

            // Draw handle outer circle (border)
            drawCircle(
                color = handleColor,
                radius = handleRadiusPx,
                center = Offset(handleX, handleY)
            )

            // Draw handle inner circle (white center)
            drawCircle(
                color = Color.White,
                radius = handleRadiusPx - 4.dp.toPx(),
                center = Offset(handleX, handleY)
            )
        }

        // Display time in center
        Text(
            text = currentTime.format(formatter),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Calculate the duration in minutes between two times, handling midnight crossing.
 */
private fun calculateDurationMinutes(startTime: LocalTime, endTime: LocalTime): Int {
    return if (endTime.isAfter(startTime)) {
        Duration.between(startTime, endTime).toMinutes().toInt()
    } else {
        // Crosses midnight
        val toMidnight = Duration.between(startTime, LocalTime.MAX).toMinutes().toInt() + 1
        val fromMidnight = Duration.between(LocalTime.MIDNIGHT, endTime).toMinutes().toInt()
        toMidnight + fromMidnight
    }
}

/**
 * Calculate the progress (0.0 to 1.0) for a given time within the range.
 */
private fun calculateProgress(startTime: LocalTime, currentTime: LocalTime, totalMinutes: Int): Float {
    if (totalMinutes == 0) return 0f

    val elapsedMinutes = if (currentTime.isAfter(startTime) || currentTime == startTime) {
        Duration.between(startTime, currentTime).toMinutes().toInt()
    } else {
        // Current time is after midnight
        val toMidnight = Duration.between(startTime, LocalTime.MAX).toMinutes().toInt() + 1
        val fromMidnight = Duration.between(LocalTime.MIDNIGHT, currentTime).toMinutes().toInt()
        toMidnight + fromMidnight
    }

    return (elapsedMinutes.toFloat() / totalMinutes).coerceIn(0f, 1f)
}

/**
 * Calculate the time from a progress value.
 */
private fun calculateTimeFromProgress(startTime: LocalTime, progress: Float, totalMinutes: Int): LocalTime {
    val elapsedMinutes = (progress * totalMinutes).roundToInt()
    return startTime.plusMinutes(elapsedMinutes.toLong())
}

/**
 * Calculate progress from a touch/drag offset position.
 */
private fun calculateProgressFromOffset(
    offset: Offset,
    center: Offset,
    totalMinutes: Int,
    gapAngle: Float
): Float {
    val dx = offset.x - center.x
    val dy = offset.y - center.y

    // Calculate angle from top (12 o'clock position) in degrees
    var angle = Math.toDegrees(atan2(dx.toDouble(), -dy.toDouble()))
    if (angle < 0) angle += 360.0

    // The arc starts at (gapAngle/2) degrees from top and spans (360 - gapAngle) degrees
    val halfGap = gapAngle / 2

    // Adjust angle to be relative to the arc start
    // Arc starts at halfGap degrees clockwise from top
    var relativeAngle = angle - halfGap
    if (relativeAngle < 0) relativeAngle += 360.0

    // The usable arc is (360 - gapAngle) degrees
    val usableArc = 360.0 - gapAngle

    // If the angle is in the gap area, clamp to nearest end
    if (relativeAngle > usableArc) {
        // In the gap - determine which end is closer
        val distanceToEnd = relativeAngle - usableArc
        val distanceToStart = 360.0 - relativeAngle
        relativeAngle = if (distanceToEnd < distanceToStart) usableArc else 0.0
    }

    // Convert to progress
    val rawProgress = (relativeAngle / usableArc).toFloat()

    // Snap to minute increments
    val minuteIncrement = 1f / totalMinutes
    val snappedProgress = (rawProgress / minuteIncrement).roundToInt() * minuteIncrement

    return snappedProgress.coerceIn(0f, 1f)
}
