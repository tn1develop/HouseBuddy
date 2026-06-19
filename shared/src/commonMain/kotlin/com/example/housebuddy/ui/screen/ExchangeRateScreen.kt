package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.presentation.mvi.ExchangeRateEvent
import com.example.housebuddy.presentation.mvi.ExchangeRateViewState
import com.example.housebuddy.ui.components.MonthlyLineChart
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.ecb_loading
import housebuddy.shared.generated.resources.euribor_chart_description
import housebuddy.shared.generated.resources.euribor_disclaimer
import housebuddy.shared.generated.resources.euribor_history_title
import housebuddy.shared.generated.resources.euribor_mortgage_explanation
import housebuddy.shared.generated.resources.exchange_rate_load_error
import housebuddy.shared.generated.resources.latest_month
import housebuddy.shared.generated.resources.no_data_available
import housebuddy.shared.generated.resources.period_range
import housebuddy.shared.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExchangeRateScreen(
    state: ExchangeRateViewState = ExchangeRateViewState(),
    onIntent: (ExchangeRateEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onIntent(ExchangeRateEvent.ScreenOpened)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.euribor_history_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(Res.string.euribor_chart_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(Res.string.euribor_mortgage_explanation),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(Res.string.euribor_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(Res.string.ecb_loading),
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            state.loadFailed -> {
                Text(
                    text = stringResource(Res.string.exchange_rate_load_error),
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = { onIntent(ExchangeRateEvent.RetryClicked) }) {
                    Text(stringResource(Res.string.retry))
                }
            }

            state.monthlyRates.isEmpty() -> {
                Text(text = stringResource(Res.string.no_data_available))
            }

            else -> {
                val latest = state.monthlyRates.last()
                val earliest = state.monthlyRates.first()
                Text(
                    text = stringResource(
                        Res.string.latest_month,
                        latest.yearMonth,
                        formatNumber(latest.averageRate, 2)
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(
                        Res.string.period_range,
                        earliest.yearMonth,
                        latest.yearMonth,
                        state.monthlyRates.size
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                MonthlyLineChart(
                    data = state.monthlyRates,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
