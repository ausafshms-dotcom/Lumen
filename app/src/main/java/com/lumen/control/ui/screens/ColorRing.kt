package com.lumen.control.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointerInput
import androidx.compose.ui.unit.dp
import com.lumen.control.ir.LightCommand
import kotlin.math.atan2
import kotlin.math.min

data class ColorSwatch(val command: LightCommand, val color: Color)

val ringSwatches = listOf(
    ColorSwatch(LightCommand.RED, Color(0xFFFF3B30)),
    ColorSwatch(LightCommand.LIGHT_RED, Color(0xFFFF7A70)),
    ColorSwatch(LightCommand.ORANGE, Color(0xFFFF9500)),
    ColorSwatch(LightCommand.LIGHT_ORANGE, Color(0xFFFFC266)),
    ColorSwatch(LightCommand.YELLOW, Color(0xFFFFD60A)),
    ColorSwatch(LightCommand.LIME_GREEN, Color(0xFFB2FF3A)),
    ColorSwatch(LightCommand.GREEN, Color(0xFF34C759)),
    ColorSwatch(LightCommand.LIGHT_GREEN, Color(0xFF7BE0A0)),
    ColorSwatch(LightCommand.TEAL, Color(0xFF20C5B0)),
    ColorSwatch(LightCommand.CYAN, Color(0xFF32D8E0)),
    ColorSwatch(LightCommand.LIGHT_BLUE, Color(0xFF69C7FF)),
    ColorSwatch(LightCommand.BLUE, Color(0xFF3478F6)),
    ColorSwatch(LightCommand.PURPLE, Color(0xFF8B4DFF)),
    ColorSwatch(LightCommand.DARK_PURPLE, Color(0xFF5A2D9C)),
    ColorSwatch(LightCommand.PINK, Color(0xFFFF5CA8))
)

/**
 * A tap-to-select color ring. Each wedge maps to one fixed IR color code
 * (the hardware only supports discrete colors, not arbitrary RGB), and the
 * center button fires White. Selected wedge gets a highlight ring.
 */
@Composable
fun ColorRing(
    selected: LightCommand?,
    onColorSelected: (LightCommand) -> Unit,
    onWhiteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sweepPerSwatch = 360f / ringSwatches.size

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val radius = min(size.width, size.height) / 2f
                        val distFromCenter = kotlin.math.sqrt(dx * dx + dy * dy)

                        // Ignore taps in the empty hole/center area
                        if (distFromCenter < radius * 0.42f) return@detectTapGestures
                        if (distFromCenter > radius) return@detectTapGestures

                        var angle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                        angle = (angle + 90 + 360) % 360 // rotate so 0deg = top
                        val index = (angle / sweepPerSwatch).toInt().coerceIn(0, ringSwatches.lastIndex)
                        onColorSelected(ringSwatches[index].command)
                    }
                }
        ) {
            val strokeWidth = size.minDimension * 0.30f
            val ringRadius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - ringRadius * 2) / 2f,
                (size.height - ringRadius * 2) / 2f
            )

            ringSwatches.forEachIndexed { index, swatch ->
                val startAngle = -90f + index * sweepPerSwatch
                val isSelected = swatch.command == selected
                drawArc(
                    color = swatch.color,
                    startAngle = startAngle + 1.2f,
                    sweepAngle = sweepPerSwatch - 2.4f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(ringRadius * 2, ringRadius * 2),
                    style = Stroke(width = if (isSelected) strokeWidth else strokeWidth * 0.82f)
                )
            }
        }

        // Center "White" button
        val glow by animateFloatAsState(
            targetValue = if (selected == LightCommand.WHITE) 1f else 0f,
            animationSpec = spring(),
            label = "whiteGlow"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.36f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF2A2A38), Color(0xFF15151D))
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures { onWhiteSelected() }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "White",
                tint = Color.White.copy(alpha = 0.6f + glow * 0.4f)
            )
        }
    }
}
