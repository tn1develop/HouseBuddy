package com.example.housebuddy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.housebuddy.domain.model.MonthlyExchangeRate
import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.ui.theme.Purple40
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun MonthlyLineChart(
    data: List<MonthlyExchangeRate>,
    modifier: Modifier = Modifier,
    chartHeight: androidx.compose.ui.unit.Dp = 200.dp,
    pointSpacing: androidx.compose.ui.unit.Dp = 22.dp,
    lineColor: Color = Purple40,
    gridColor: Color = Color.LightGray.copy(alpha = 0.5f)
) {
    if (data.isEmpty()) return

    var selectedIndex by remember(data) { mutableIntStateOf(-1) }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val axisLabelStyle = TextStyle(
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val selectedLabelStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val pointSpacingPx = with(density) { pointSpacing.toPx() }
    val chartContentWidth = (data.size * pointSpacing.value).coerceAtLeast(280f).dp
    val yAxisWidth = 44.dp
    val xAxisHeight = 36.dp
    val paddingLeft = 8.dp
    val paddingRight = 16.dp
    val paddingTop = 32.dp
    val totalScrollWidth = chartContentWidth + paddingLeft + paddingRight

    val minValue = remember(data) { floor(data.minOf { it.averageRate } * 100) / 100 }
    val maxValue = remember(data) { ceil(data.maxOf { it.averageRate } * 100) / 100 }
    val valueRange = (maxValue - minValue).coerceAtLeast(0.01)
    val yAxisSteps = 4
    val yTicks = remember(minValue, maxValue) {
        (0..yAxisSteps).map { step ->
            minValue + (maxValue - minValue) * step / yAxisSteps
        }
    }
    val xLabelStep = remember(data.size) {
        when {
            data.size <= 24 -> 3
            data.size <= 72 -> 6
            else -> 12
        }
    }
    val xLabelIndices = remember(data, xLabelStep) {
        buildList {
            for (index in data.indices step xLabelStep) add(index)
            if (data.isNotEmpty() && last() != data.lastIndex) add(data.lastIndex)
        }
    }

    val scrollState = rememberScrollState()
    LaunchedEffect(data.size) {
        delay(100)
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Euribor 3M (%)",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = yAxisWidth, bottom = 4.dp)
        )
        Row(Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .width(yAxisWidth)
                    .height(chartHeight)
            ) {
                val plotHeight = size.height - paddingTop.toPx()
                val top = paddingTop.toPx()
                val bottom = size.height
                yTicks.forEach { tick ->
                    val fraction = ((tick - minValue) / valueRange).toFloat()
                    val y = bottom - fraction * plotHeight
                    val label = formatYAxis(tick)
                    val layout = textMeasurer.measure(label, axisLabelStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        style = axisLabelStyle,
                        topLeft = Offset(
                            x = size.width - layout.size.width - 4.dp.toPx(),
                            y = y - layout.size.height / 2f
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(chartHeight + xAxisHeight)
                    .horizontalScroll(scrollState)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(totalScrollWidth)
                        .height(chartHeight + xAxisHeight)
                        .pointerInput(data, pointSpacingPx) {
                            val left = paddingLeft.toPx()
                            val plotBottom = chartHeight.toPx()
                            val top = paddingTop.toPx()
                            detectTapGestures { offset ->
                                if (offset.y < top || offset.y > plotBottom) return@detectTapGestures
                                val index = ((offset.x - left) / pointSpacingPx)
                                    .roundToInt()
                                    .coerceIn(0, data.lastIndex)
                                selectedIndex = index
                            }
                        }
                ) {
                    val left = paddingLeft.toPx()
                    val right = size.width - paddingRight.toPx()
                    val top = paddingTop.toPx()
                    val bottom = chartHeight.toPx()
                    val plotHeight = bottom - top

                    repeat(yAxisSteps + 1) { index ->
                        val fraction = index.toFloat() / yAxisSteps
                        val y = bottom - fraction * plotHeight
                        drawLine(
                            color = gridColor,
                            start = Offset(left, y),
                            end = Offset(right, y),
                            strokeWidth = 1f
                        )
                    }

                    val path = Path()
                    data.forEachIndexed { index, point ->
                        val x = left + index * pointSpacingPx
                        val normalized = ((point.averageRate - minValue) / valueRange).toFloat()
                        val y = bottom - normalized * plotHeight
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    data.forEachIndexed { index, point ->
                        if (index == selectedIndex) return@forEachIndexed
                        val x = left + index * pointSpacingPx
                        val normalized = ((point.averageRate - minValue) / valueRange).toFloat()
                        val y = bottom - normalized * plotHeight
                        drawCircle(color = lineColor, radius = 3.5f, center = Offset(x, y))
                    }

                    if (selectedIndex in data.indices) {
                        val point = data[selectedIndex]
                        val x = left + selectedIndex * pointSpacingPx
                        val normalized = ((point.averageRate - minValue) / valueRange).toFloat()
                        val y = bottom - normalized * plotHeight
                        val center = Offset(x, y)

                        drawLine(
                            color = lineColor.copy(alpha = 0.35f),
                            start = Offset(x, top),
                            end = Offset(x, bottom),
                            strokeWidth = 1f
                        )
                        drawCircle(color = lineColor, radius = 8f, center = center)
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = center
                        )

                        val label = formatSelectedLabel(point)
                        val layout = textMeasurer.measure(label, selectedLabelStyle)
                        val labelPadding = 6.dp.toPx()
                        val labelTop = (y - layout.size.height - 14.dp.toPx()).coerceAtLeast(top)
                        val labelLeft = (x - layout.size.width / 2f).coerceIn(
                            left,
                            right - layout.size.width
                        )
                        drawRoundRect(
                            color = labelBackgroundColor,
                            topLeft = Offset(
                                labelLeft - labelPadding,
                                labelTop - labelPadding
                            ),
                            size = Size(
                                layout.size.width + labelPadding * 2,
                                layout.size.height + labelPadding * 2
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                        )
                        drawRoundRect(
                            color = lineColor.copy(alpha = 0.25f),
                            topLeft = Offset(
                                labelLeft - labelPadding,
                                labelTop - labelPadding
                            ),
                            size = Size(
                                layout.size.width + labelPadding * 2,
                                layout.size.height + labelPadding * 2
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                            style = Stroke(width = 1f)
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            style = selectedLabelStyle,
                            topLeft = Offset(labelLeft, labelTop)
                        )
                    }

                    xLabelIndices.forEach { index ->
                        val x = left + index * pointSpacingPx
                        val label = formatXAxis(data[index].yearMonth)
                        val layout = textMeasurer.measure(label, axisLabelStyle)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            style = axisLabelStyle,
                            topLeft = Offset(
                                x = x - layout.size.width / 2f,
                                y = bottom + 6.dp.toPx()
                            )
                        )
                    }
                }
            }
        }
        Text(
            text = "Mese / Anno",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

private fun formatSelectedLabel(point: MonthlyExchangeRate): String {
    return "${formatXAxis(point.yearMonth)} · ${formatNumber(point.averageRate, 2)}%"
}

private fun formatYAxis(value: Double): String = formatNumber(value, 2)

private fun formatXAxis(yearMonth: String): String {
    if (yearMonth.length < 7) return yearMonth
    val year = yearMonth.substring(2, 4)
    val month = yearMonth.substring(5, 7)
    return "$month/$year"
}
