package com.example.housebuddy.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.model.SimulateScenariosResult

data class ScenarioBarChartItem(
    val year: Int,
    val costoTotaleCasa: Double?,
    val convenienceRank: ScenarioBarConvenienceRank = ScenarioBarConvenienceRank.None
)

enum class ScenarioBarConvenienceRank {
    None,
    MostConvenient,
    SecondConvenient,
    ThirdConvenient,
    LeastConvenient,
    SecondLeastConvenient,
    ThirdLeastConvenient
}

object ScenarioBarChartColors {
    const val DEFAULT_FILL: Long = 0xFFCCCCCC
    const val MOST_CONVENIENT_FILL: Long = 0xFF1B5E20
    const val SECOND_CONVENIENT_FILL: Long = 0xFF388E3C
    const val THIRD_CONVENIENT_FILL: Long = 0xFF81C784
    const val LEAST_CONVENIENT_FILL: Long = 0xFFB71C1C
    const val SECOND_LEAST_CONVENIENT_FILL: Long = 0xFFD32F2F
    const val THIRD_LEAST_CONVENIENT_FILL: Long = 0xFFE57373
}

fun SimulateScenariosResult.toBarChartItems(): List<ScenarioBarChartItem> =
    scenariosByYear.entries
        .sortedBy { it.key }
        .map { (year, result) ->
            ScenarioBarChartItem(year = year, costoTotaleCasa = result?.costoTotaleCasa)
        }
        .withConvenienceRanks()

private fun List<ScenarioBarChartItem>.withConvenienceRanks(): List<ScenarioBarChartItem> {
    val indexedCosts = mapIndexedNotNull { index, item ->
        item.costoTotaleCasa?.let { index to it }
    }

    val mostConvenientIndices = indexedCosts
        .sortedWith(compareBy<Pair<Int, Double>> { it.second }.thenBy { it.first })
        .take(3)
        .map { it.first }

    val leastConvenientIndices = indexedCosts
        .filter { it.first !in mostConvenientIndices }
        .sortedWith(compareByDescending<Pair<Int, Double>> { it.second }.thenBy { it.first })
        .take(3)
        .map { it.first }

    val rankByIndex = buildMap {
        mostConvenientIndices.forEachIndexed { rank, index ->
            put(
                index,
                when (rank) {
                    0 -> ScenarioBarConvenienceRank.MostConvenient
                    1 -> ScenarioBarConvenienceRank.SecondConvenient
                    else -> ScenarioBarConvenienceRank.ThirdConvenient
                }
            )
        }
        leastConvenientIndices.forEachIndexed { rank, index ->
            put(
                index,
                when (rank) {
                    0 -> ScenarioBarConvenienceRank.LeastConvenient
                    1 -> ScenarioBarConvenienceRank.SecondLeastConvenient
                    else -> ScenarioBarConvenienceRank.ThirdLeastConvenient
                }
            )
        }
    }

    return mapIndexed { index, item ->
        item.copy(convenienceRank = rankByIndex[index] ?: ScenarioBarConvenienceRank.None)
    }
}

fun ScenarioBarChartItem.barFillColor(): Long = when (convenienceRank) {
    ScenarioBarConvenienceRank.MostConvenient -> ScenarioBarChartColors.MOST_CONVENIENT_FILL
    ScenarioBarConvenienceRank.SecondConvenient -> ScenarioBarChartColors.SECOND_CONVENIENT_FILL
    ScenarioBarConvenienceRank.ThirdConvenient -> ScenarioBarChartColors.THIRD_CONVENIENT_FILL
    ScenarioBarConvenienceRank.LeastConvenient -> ScenarioBarChartColors.LEAST_CONVENIENT_FILL
    ScenarioBarConvenienceRank.SecondLeastConvenient -> ScenarioBarChartColors.SECOND_LEAST_CONVENIENT_FILL
    ScenarioBarConvenienceRank.ThirdLeastConvenient -> ScenarioBarChartColors.THIRD_LEAST_CONVENIENT_FILL
    ScenarioBarConvenienceRank.None -> ScenarioBarChartColors.DEFAULT_FILL
}

@Composable
fun ScenarioMirroredBarChart(
    items: List<ScenarioBarChartItem>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 220.dp,
    columnWidth: Dp = 28.dp
) {
    if (items.isEmpty()) return

    val scrollState = rememberScrollState()
    val chartWidth = columnWidth * items.size

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        ScenarioMirroredBarChartNative(
            items = items,
            modifier = Modifier
                .width(chartWidth)
                .height(chartHeight),
            chartHeight = chartHeight,
            columnWidth = columnWidth
        )
    }
}

@Composable
expect fun ScenarioMirroredBarChartNative(
    items: List<ScenarioBarChartItem>,
    modifier: Modifier,
    chartHeight: Dp,
    columnWidth: Dp
)
