package com.qizzy9.cryptotracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SparklineChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF3FB950),
    showGradient: Boolean = true,
) {
    if (data.size < 2) return

    Canvas(modifier = modifier) {
        val min = data.min()
        val max = data.max()
        val range = (max - min).takeIf { it != 0.0 } ?: 1.0

        val stepX = size.width / (data.size - 1)

        fun xOf(i: Int) = i * stepX
        fun yOf(v: Double) = size.height - ((v - min) / range * size.height).toFloat()

        val linePath = Path().apply {
            moveTo(xOf(0), yOf(data[0]))
            for (i in 1 until data.size) {
                lineTo(xOf(i), yOf(data[i]))
            }
        }

        if (showGradient) {
            val fillPath = Path().apply {
                moveTo(xOf(0), size.height)
                lineTo(xOf(0), yOf(data[0]))
                for (i in 1 until data.size) lineTo(xOf(i), yOf(data[i]))
                lineTo(xOf(data.size - 1), size.height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                ),
            )
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )

        drawCircle(
            color = lineColor,
            radius = 6f,
            center = Offset(xOf(data.size - 1), yOf(data.last())),
        )
    }
}
