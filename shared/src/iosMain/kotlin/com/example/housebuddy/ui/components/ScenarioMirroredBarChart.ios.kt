package com.example.housebuddy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.UIView
import kotlin.math.max
import kotlin.math.PI

private const val CENTER_BAND_HEIGHT_DP = 36.0
private const val LABEL_TEXT_SIZE_SP = 11.0

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ScenarioMirroredBarChartNative(
    items: List<ScenarioBarChartItem>,
    modifier: Modifier,
    chartHeight: Dp,
    columnWidth: Dp
) {
    val density = LocalDensity.current
    val chartHeightPx = with(density) { chartHeight.value.toDouble() * density.density }
    val columnWidthPx = with(density) { columnWidth.value.toDouble() * density.density }
    val centerBandHeightPx = with(density) { CENTER_BAND_HEIGHT_DP * density.density }
    val labelTextSizePx = with(density) { LABEL_TEXT_SIZE_SP * density.density }

    UIKitView(
        factory = {
            ScenarioMirroredBarChartUIView().apply {
                updateChart(
                    items = items,
                    chartHeightPx = chartHeightPx,
                    columnWidthPx = columnWidthPx,
                    centerBandHeightPx = centerBandHeightPx,
                    labelTextSizePx = labelTextSizePx
                )
            }
        },
        modifier = modifier,
        update = { view ->
            view.updateChart(
                items = items,
                chartHeightPx = chartHeightPx,
                columnWidthPx = columnWidthPx,
                centerBandHeightPx = centerBandHeightPx,
                labelTextSizePx = labelTextSizePx
            )
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class ScenarioMirroredBarChartUIView : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    private var items: List<ScenarioBarChartItem> = emptyList()
    private var chartHeightPx: Double = 0.0
    private var columnWidthPx: Double = 0.0
    private var centerBandHeightPx: Double = 0.0
    private var labelTextSizePx: Double = 0.0

    private val strokeColor = UIColor.blackColor

    fun updateChart(
        items: List<ScenarioBarChartItem>,
        chartHeightPx: Double,
        columnWidthPx: Double,
        centerBandHeightPx: Double,
        labelTextSizePx: Double
    ) {
        this.items = items
        this.chartHeightPx = chartHeightPx
        this.columnWidthPx = columnWidthPx
        this.centerBandHeightPx = centerBandHeightPx
        this.labelTextSizePx = labelTextSizePx
        val width = items.size * columnWidthPx
        setFrame(CGRectMake(0.0, 0.0, width, chartHeightPx))
        rebuildSubviews()
    }

    private fun rebuildSubviews() {
        subviews.forEach { (it as UIView).removeFromSuperview() }
        if (items.isEmpty() || columnWidthPx <= 0.0 || chartHeightPx <= 0.0) return

        val height = chartHeightPx
        val centerY = height / 2.0
        val halfBand = centerBandHeightPx / 2.0
        val maxBarHeight = max((height - centerBandHeightPx) / 2.0, 0.0)
        val maxValue = items.mapNotNull { it.totalHouseCost }.maxOrNull() ?: 0.0
        val scaleMax = max(maxValue, 1.0)

        layer.borderWidth = 1.0
        layer.borderColor = strokeColor.CGColor

        items.forEachIndexed { index, item ->
            val left = index * columnWidthPx
            val barHeight = item.totalHouseCost?.let { value ->
                (value / scaleMax * maxBarHeight)
            } ?: 0.0

            if (barHeight > 0.0) {
                val topBarBottom = centerY - halfBand
                val topBarTop = topBarBottom - barHeight
                addSubview(createBarView(left, topBarTop, columnWidthPx, barHeight, item))

                val bottomBarTop = centerY + halfBand
                addSubview(createBarView(left, bottomBarTop, columnWidthPx, barHeight, item))
            }

            if (index < items.lastIndex) {
                val divider = UIView(frame = CGRectMake(left + columnWidthPx - 0.5, 0.0, 1.0, height))
                divider.backgroundColor = strokeColor
                addSubview(divider)
            }

            val label = UILabel(frame = CGRectMake(left, centerY - halfBand, columnWidthPx, centerBandHeightPx))
            label.text = item.year.toString()
            label.textColor = strokeColor
            label.font = platform.UIKit.UIFont.systemFontOfSize(labelTextSizePx)
            label.textAlignment = platform.UIKit.NSTextAlignmentCenter
            label.transform = platform.CoreGraphics.CGAffineTransformMakeRotation(-PI / 2)
            addSubview(label)
        }
    }

    private fun createBarView(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        item: ScenarioBarChartItem
    ): UIView {
        val bar = UIView(frame = CGRectMake(x, y, width, height))
        bar.backgroundColor = item.barFillColor().toUiColor()
        bar.layer.borderWidth = 1.0
        bar.layer.borderColor = strokeColor.CGColor
        return bar
    }
}

private fun Long.toUiColor(): UIColor {
    val r = ((this shr 16) and 0xFF) / 255.0
    val g = ((this shr 8) and 0xFF) / 255.0
    val b = (this and 0xFF) / 255.0
    return UIColor.colorWithRed(r, g, b, 1.0)
}
