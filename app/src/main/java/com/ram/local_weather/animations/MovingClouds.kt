package com.ram.local_weather.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

//@Composable
//fun MovingCloudsAnimation(
//    modifier: Modifier = Modifier,
//    cloudColor: Color = Color(0xFFE0E0E0),
//    cloudCount: Int = 5,
//    cloudSpeed: Float = 20f // lower = slower, higher = faster
//) {
//    val infiniteTransition = rememberInfiniteTransition(label = "cloud_move")
//
//    // Animate a continuous horizontal offset
//    val offsetX by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1000f, // movement distance before looping
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = (60000 / cloudSpeed).toInt(), easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "cloud_offset"
//    )
//
//    Canvas(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .background(Color(0xFF87CEEB)) // sky blue
//    ) {
//        val width = size.width
//        val height = size.height
//
//        // Draw multiple clouds at different heights and scales
//        for (i in 0 until cloudCount) {
//            val scale = 2f + (i * 0.1f)
//            val y = height * (0.1f + (i * 0.15f))
//            val x = ((offsetX + i * width / cloudCount) % width)
//
//            drawCloud(
//                center = Offset(x, y),
//                scale = scale,
//                color = cloudColor
//            )
//        }
//    }
//}
//
//fun DrawScope.drawCloud(center: Offset, scale: Float, color: Color) {
//    val radius = 40f * scale
//
//    drawCircle(color = color, radius = radius*0.8f, center = center)
//    drawCircle(color = color, radius = radius * 0.8f, center = center + Offset(radius / 1.2f, radius / 2))
//    drawCircle(color = color, radius = radius * 0.8f, center = center + Offset(radius /6 , radius / 2))
//    drawCircle(color = color, radius = radius * 0.8f, center = center + Offset(-radius / 1.2f, radius / 2))
//}

@Composable
fun MovingCloudsAnimation(
    modifier: Modifier = Modifier,
    cloudColor: Color = Color(0xFFE0E0E0),
    cloudCount: Int = 5,
    cloudSpeed: Float = 20f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud_move")

    // Animate X offset
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f, // use normalized 0..1 range
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (60000 / cloudSpeed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_offset"
    )

    // Precompute static cloud parameters (y & scale) once
    val cloudData = remember {
        List(cloudCount) { index ->
            CloudData(
                yFactor = 0.1f + (index * 0.15f),
                scale = 2f + (index * 0.1f),
                offsetFactor = index / cloudCount.toFloat()
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFF87CEEB)) // Sky blue
    ) {
        val width = size.width
        val height = size.height

        cloudData.forEach { cloud ->
            val x = ((offsetX + cloud.offsetFactor) % 1f) * width
            val y = height * cloud.yFactor
            drawCloud(center = Offset(x, y), scale = cloud.scale, color = cloudColor)
        }
    }
}

private data class CloudData(
    val yFactor: Float,
    val scale: Float,
    val offsetFactor: Float
)

fun DrawScope.drawCloud(center: Offset, scale: Float, color: Color) {
    val baseRadius = 40f * scale
    val centers = listOf(
        center,
        center + Offset(baseRadius / 1.2f, baseRadius / 2),
        center + Offset(baseRadius / 6, baseRadius / 2),
        center + Offset(-baseRadius / 1.2f, baseRadius / 2)
    )

    centers.forEach {
        drawCircle(
            color = color,
            radius = baseRadius * 0.8f,
            center = it
        )
    }
}

