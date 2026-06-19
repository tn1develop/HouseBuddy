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
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.annual_savings_label
import housebuddy.shared.generated.resources.chart_legend
import housebuddy.shared.generated.resources.current_liquidity_label
import housebuddy.shared.generated.resources.rent_payment_label
import housebuddy.shared.generated.resources.suffix_eur
import housebuddy.shared.generated.resources.yearly_total_cost_label
import org.jetbrains.compose.resources.stringResource

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
            label = stringResource(Res.string.rent_payment_label),
            value = state.rentPaymentInput,
            onValueChange = { onIntent(HousePriceEvent.RentPaymentChanged(it)) },
            onStep = { onIntent(HousePriceEvent.RentPaymentStepped(it)) },
            suffix = stringResource(Res.string.suffix_eur),
            topPadding = 0.dp
        )
        StepperInputField(
            label = stringResource(Res.string.current_liquidity_label),
            value = state.currentLiquidityInput,
            onValueChange = { onIntent(HousePriceEvent.CurrentLiquidityChanged(it)) },
            onStep = { onIntent(HousePriceEvent.CurrentLiquidityStepped(it)) },
            suffix = stringResource(Res.string.suffix_eur)
        )
        StepperInputField(
            label = stringResource(Res.string.annual_savings_label),
            value = state.annualSavingsInput,
            onValueChange = { onIntent(HousePriceEvent.AnnualSavingsChanged(it)) },
            onStep = { onIntent(HousePriceEvent.AnnualSavingsStepped(it)) },
            suffix = stringResource(Res.string.suffix_eur)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Text(
            text = stringResource(Res.string.yearly_total_cost_label),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Text(
            text = stringResource(Res.string.chart_legend),
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
