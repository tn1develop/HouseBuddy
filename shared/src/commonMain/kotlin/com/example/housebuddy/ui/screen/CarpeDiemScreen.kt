package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.model.SimulateScenariosResult
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.ScenarioMirroredBarChart
import com.example.housebuddy.ui.components.StepperInputField
import com.example.housebuddy.ui.components.toBarChartItems

@Composable
fun CarpeDiemScreen(
    state: HousePriceViewState,
    scenarioResult: SimulateScenariosResult,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StepperInputField(
            label = "Canone affitto (no bollette)",
            value = state.canoneAffittoInput,
            onValueChange = { onIntent(HousePriceEvent.CanoneAffittoChanged(it)) },
            onStep = { onIntent(HousePriceEvent.CanoneAffittoStepped(it)) },
            suffix = "EUR",
            topPadding = 0.dp
        )
        StepperInputField(
            label = "Liquidità attuale",
            value = state.liquiditaAttualeInput,
            onValueChange = { onIntent(HousePriceEvent.LiquiditaAttualeChanged(it)) },
            onStep = { onIntent(HousePriceEvent.LiquiditaAttualeStepped(it)) },
            suffix = "EUR"
        )
        StepperInputField(
            label = "Risparmio annuale",
            value = state.risparmioAnnualeInput,
            onValueChange = { onIntent(HousePriceEvent.RisparmioAnnualeChanged(it)) },
            onStep = { onIntent(HousePriceEvent.RisparmioAnnualeStepped(it)) },
            suffix = "EUR"
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Text(
            text = "Costo totale per anno",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Text(
            text = "Altezza barra = costo totale casa + affitto.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        ScenarioMirroredBarChart(
            items = scenarioResult.toBarChartItems(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
