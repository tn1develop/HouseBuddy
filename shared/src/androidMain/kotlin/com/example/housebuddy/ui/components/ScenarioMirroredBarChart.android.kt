package com.example.housebuddy.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.max

private const val BAR_STROKE_COLOR = Color.BLACK
private const val CENTER_BAND_HEIGHT_DP = 36f
private const val LABEL_TEXT_SIZE_SP = 11f

@Composable
actual fun ScenarioMirroredBarChartNative(
    items: List<ScenarioBarChartItem>,
    modifier: Modifier,
    chartHeight: Dp,
    columnWidth: Dp
) {
    val density = LocalDensity.current
    val chartHeightPx = with(density) { chartHeight.roundToPx() }
    val columnWidthPx = with(density) { columnWidth.roundToPx() }
    val centerBandHeightPx = with(density) { CENTER_BAND_HEIGHT_DP.dp.roundToPx() }
    val labelTextSizePx = with(density) { LABEL_TEXT_SIZE_SP.sp.toPx() }

    AndroidView(
        factory = { context ->
            ScenarioMirroredBarChartView(context).apply {
                setChartMetrics(chartHeightPx, columnWidthPx, centerBandHeightPx, labelTextSizePx)
                setItems(items)
            }
        },
        update = { view ->
            view.setChartMetrics(chartHeightPx, columnWidthPx, centerBandHeightPx, labelTextSizePx)
            view.setItems(items)
        },
        modifier = modifier
    )
}

private class ScenarioMirroredBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var items: List<ScenarioBarChartItem> = emptyList()
    private var chartHeightPx: Int = 0
    private var columnWidthPx: Int = 0
    private var centerBandHeightPx: Int = 0
    private var labelTextSizePx: Float = 0f

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = BAR_STROKE_COLOR
        strokeWidth = 1f
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = BAR_STROKE_COLOR
        textAlign = Paint.Align.CENTER
    }
    private val barRect = RectF()

    fun setChartMetrics(
        chartHeightPx: Int,
        columnWidthPx: Int,
        centerBandHeightPx: Int,
        labelTextSizePx: Float
    ) {
        this.chartHeightPx = chartHeightPx
        this.columnWidthPx = columnWidthPx
        this.centerBandHeightPx = centerBandHeightPx
        this.labelTextSizePx = labelTextSizePx
        labelPaint.textSize = labelTextSizePx
        invalidate()
    }

    fun setItems(items: List<ScenarioBarChartItem>) {
        this.items = items
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (items.isEmpty()) 0 else items.size * columnWidthPx
        val height = chartHeightPx.coerceAtLeast(0)
        setMeasuredDimension(
            resolveSize(width, widthMeasureSpec),
            resolveSize(height, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (items.isEmpty() || columnWidthPx <= 0 || chartHeightPx <= 0) return

        val centerY = height / 2f
        val halfBand = centerBandHeightPx / 2f
        val maxBarHeight = max((height - centerBandHeightPx) / 2f, 0f)
        val maxValue = items.mapNotNull { it.totalHouseCost }.maxOrNull() ?: 0.0
        val scaleMax = max(maxValue, 1.0)

        items.forEachIndexed { index, item ->
            val left = index * columnWidthPx.toFloat()
            val right = left + columnWidthPx
            val barHeight = item.totalHouseCost?.let { value ->
                (value / scaleMax * maxBarHeight).toFloat()
            } ?: 0f

            val topBarBottom = centerY - halfBand
            val topBarTop = topBarBottom - barHeight
            fillPaint.color = item.barFillColor().toInt()
            if (barHeight > 0f) {
                barRect.set(left, topBarTop, right, topBarBottom)
                canvas.drawRect(barRect, fillPaint)
                canvas.drawRect(barRect, strokePaint)
            }

            val bottomBarTop = centerY + halfBand
            val bottomBarBottom = bottomBarTop + barHeight
            if (barHeight > 0f) {
                barRect.set(left, bottomBarTop, right, bottomBarBottom)
                canvas.drawRect(barRect, fillPaint)
                canvas.drawRect(barRect, strokePaint)
            }

            if (index < items.lastIndex) {
                canvas.drawLine(right, 0f, right, height.toFloat(), strokePaint)
            }

            val label = item.year.toString()
            val textX = left + columnWidthPx / 2f
            val textY = centerY
            canvas.save()
            canvas.rotate(-90f, textX, textY)
            canvas.drawText(label, textX, textY + labelTextSizePx / 3f, labelPaint)
            canvas.restore()
        }

        canvas.drawLine(0f, 0f, width.toFloat(), 0f, strokePaint)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), strokePaint)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), strokePaint)
    }
}
