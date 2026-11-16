package com.example.ioslauncher.ui.components

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val RgbColors = listOf(
    Color(0xFF00E5FF), // cian
    Color(0xFFFF2BD6), // magenta
    Color(0xFF9D4EDD), // morado
    Color(0xFFFF8C1A), // naranja
    Color(0xFF00E5FF)
)

/**
 * Borde RGB animado estilo Apple TV / Apple Intelligence.
 * Glow con blur en Android 12+, y en versiones anteriores un halo translúcido.
 */
@Composable
fun AppleTvRgbBorder(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 0.dp,
    strokeWidth: Dp = 3.dp,
    glowWidth: Dp = 16.dp,
    glowRadius: Dp = 14.dp,
    speedMillis: Int = 4000,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "rgb")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(speedMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(modifier) {
        content()
        if (enabled) {
            val hasBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val glowModifier = if (hasBlur) Modifier.matchParentSize().blur(glowRadius)
            else Modifier.matchParentSize()
            val glowAlpha = if (hasBlur) 0.9f else 0.35f

            Canvas(glowModifier) {
                drawRgbRing(angle, cornerRadius, glowWidth, glowAlpha)
            }
            Canvas(Modifier.matchParentSize()) {
                drawRgbRing(angle, cornerRadius, strokeWidth, 1f)
            }
        }
    }
}

private fun DrawScope.drawRgbRing(angle: Float, corner: Dp, width: Dp, alpha: Float) {
    val strokePx = width.toPx()
    val inset = strokePx / 2f
    rotate(degrees = angle) {
        drawRoundRect(
            brush = Brush.sweepGradient(RgbColors, center),
            topLeft = Offset(inset, inset),
            size = Size(size.width - strokePx, size.height - strokePx),
            cornerRadius = CornerRadius(corner.toPx()),
            style = Stroke(width = strokePx),
            alpha = alpha
        )
    }
}
