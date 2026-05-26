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
            text = "Andamento storico Euribor 3 mesi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Euribor 3 mesi (area euro), fonte BCE. Scorri il grafico per la cronologia completa.",
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
                        text = "Caricamento dati ECB…",
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = { onIntent(ExchangeRateEvent.RetryClicked) }) {
                    Text("Riprova")
                }
            }

            state.monthlyRates.isEmpty() -> {
                Text(text = "Nessun dato disponibile.")
            }

            else -> {
                val latest = state.monthlyRates.last()
                val earliest = state.monthlyRates.first()
                Text(
                    text = "Ultimo mese (${latest.yearMonth}): ${formatNumber(latest.averageRate, 2)}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Periodo: ${earliest.yearMonth} → ${latest.yearMonth} (${state.monthlyRates.size} mesi)",
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
