package com.example.szlaki.ui.components

import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    pathData: String,
    animationDuration: Int = 3000,
    strokeWidth: Float = 3f,
    color: Color = Color(0xFF4CAF50),
    isAnimated: Boolean = true,
    onAnimationFinish: () -> Unit = {}
) {
    val path = remember(pathData) {
        PathParser().parsePathString(pathData).toPath()
    }
    
    val pathLength = remember(path) {
        PathMeasure(path.asAndroidPath(), false).length
    }
    
    val animationProgress = remember { Animatable(if (isAnimated) 0f else 1f) }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animationDuration, easing = LinearEasing)
            )
            onAnimationFinish()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Skalowanie: Logo ma viewport ok 108x28
            val scale = size.width / 108f
            
            drawContext.canvas.save()
            drawContext.canvas.scale(scale, scale)
            
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth.dp.toPx() / scale,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (animationProgress.value < 1f) {
                        PathEffect.dashPathEffect(
                            intervals = floatArrayOf(pathLength, pathLength),
                            phase = (1f - animationProgress.value) * pathLength
                        )
                    } else null
                )
            )
            
            drawContext.canvas.restore()
        }
    }
}
