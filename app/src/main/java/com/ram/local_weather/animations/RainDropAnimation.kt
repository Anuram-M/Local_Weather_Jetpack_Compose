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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

//@Composable
//fun RainAnimation(
//    modifier: Modifier = Modifier,
//    dropCount: Int = 100,          // raindrops count
//    dropColor: Color = Color.Cyan,
//    dropSpeed: Float = 600f,       // pixels per second
//    dropLength: Float = 20f,       // visual length of each drop
//) {
//    val transition = rememberInfiniteTransition()
//    val time by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 2000, easing = LinearEasing)
//        )
//    )
//
//    val drops = remember(dropCount) {
//        List(dropCount) {
//            RainDrop(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                speed = Random.nextFloat() * 0.5f + 0.5f
//            )
//        }
//    }
//
//    Canvas(modifier = modifier.fillMaxSize()) {
//        val width = size.width
//        val height = size.height
//
//        drops.forEach { drop ->
//            val dropX = drop.x * width
//            val dropY = ((drop.y + time * drop.speed) % 1f) * height
//
//            drawLine(
//                color = dropColor.copy(alpha = 0.6f),
//                start = Offset(dropX, dropY),
//                end = Offset(dropX, dropY + dropLength),
//                strokeWidth = 2f
//            )
//        }
//    }
//}
//
//data class RainDrop(val x: Float, val y: Float, val speed: Float)



@Composable
fun RainAnimation(
    modifier: Modifier = Modifier,
    rainIntensity: Int = 100, // number of raindrops
    rainSpeed: Float = 400f,  // pixels per second
    angle: Float = 15f        // diagonal tilt angle in degrees
) {
    val drops = remember {
        List(rainIntensity) {
            Raindrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                length = Random.nextFloat() * 60f + 10f,
                speed = Random.nextFloat() * 0.5f + 0.75f
            )
        }
    }

    val angleRad = Math.toRadians(angle.toDouble()).toFloat()
    val sin = sin(angleRad)
    val cos = cos(angleRad)

    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val anim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain progress"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        for (drop in drops) {
            val dropX = (drop.x * width + sin * anim.value * height) % width
            val dropY = (drop.y * height + cos * anim.value * height) % height

            drawLine(
                color = Color(0xFF979797),
                start = Offset(dropX, dropY),
                end = Offset(dropX - sin * drop.length, dropY - cos * drop.length),
                strokeWidth = 2f
            )
        }
    }
}

data class Raindrop(
    val x: Float,
    val y: Float,
    val length: Float,
    val speed: Float
)
