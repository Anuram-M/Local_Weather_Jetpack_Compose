package com.ram.local_weather.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun Snowfall(modifier: Modifier = Modifier, flakeCount: Int = 100) {
    val transition = rememberInfiniteTransition(label = "snow")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val flakes = remember {
        List(flakeCount) {
            Snowflake(
                startX = Random.nextFloat(),
                startY = Random.nextFloat(),
                size = Random.nextFloat() * 12 + 2f,
                speed = Random.nextFloat() * 0.5f + 0.5f,
                seed = Random.nextFloat() * 360f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        flakes.forEach { flake ->
            val x = (flake.startX * size.width + sin(time * 6f + flake.seed) * 20f) % size.width
            val y = (flake.startY * size.height + (time * flake.speed * size.height)) % size.height
            drawSnowflake(center = Offset(x, y), size = flake.size, color = Color.LightGray.copy(alpha = 0.9f))
        }
    }
}

fun DrawScope.drawSnowflake(center: Offset, size: Float, color: Color) {
    val branches = 6
    val angleStep = 360f / branches
    for (i in 0 until branches) {
        val angle = Math.toRadians((i * angleStep).toDouble())
        val endX = center.x + cos(angle) * size
        val endY = center.y + sin(angle) * size
        drawLine(
            color = color,
            start = center,
            end = Offset(endX.toFloat(), endY.toFloat()),
            strokeWidth = size / 3
        )
    }
}


data class Snowflake(
    val startX: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val seed: Float
)
