package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.MyDropdown
import com.example.housebuddy.ui.components.StepperInputField
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.agency_commission_fixed
import housebuddy.shared.generated.resources.agency_commission_label
import housebuddy.shared.generated.resources.agency_commission_percentage
import housebuddy.shared.generated.resources.cadastral_income_label
import housebuddy.shared.generated.resources.deposit_label
import housebuddy.shared.generated.resources.green_mortgage_label
import housebuddy.shared.generated.resources.mortgage_rate_label
import housebuddy.shared.generated.resources.mortgage_years_label
import housebuddy.shared.generated.resources.number_of_buyers_label
import housebuddy.shared.generated.resources.suffix_eur
import housebuddy.shared.generated.resources.suffix_percent
import housebuddy.shared.generated.resources.suffix_years
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    state: HousePriceViewState,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val percentageLabel = stringResource(Res.string.agency_commission_percentage)
    val fixedLabel = stringResource(Res.string.agency_commission_fixed)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(Res.string.green_mortgage_label))
            Switch(
                checked = state.greenMortgage,
                onCheckedChange = { onIntent(HousePriceEvent.GreenMortgageChanged(it)) }
            )
        }
        MyDropdown(
            label = stringResource(Res.string.agency_commission_label),
            options = listOf(percentageLabel, fixedLabel),
            selectedOption = if (state.isAgencyCommissionPercentage) percentageLabel else fixedLabel,
            onOptionSelected = { onIntent(HousePriceEvent.AgencyCommissionTypeChanged(it == percentageLabel)) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.number_of_buyers_label),
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onIntent(HousePriceEvent.NumberOfBuyersStepped(-1)) }) {
                    Text("-")
                }
                OutlinedTextField(
                    value = state.numberOfBuyersInput,
                    onValueChange = { onIntent(HousePriceEvent.NumberOfBuyersChanged(it)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(56.dp)
                )
                IconButton(onClick = { onIntent(HousePriceEvent.NumberOfBuyersStepped(1)) }) {
                    Text("+")
                }
            }
        }
        StepperInputField(
            label = stringResource(Res.string.mortgage_rate_label),
            value = state.mortgageRateInput,
            onValueChange = { onIntent(HousePriceEvent.MortgageRateChanged(it)) },
            onStep = { onIntent(HousePriceEvent.MortgageRateStepped(it)) },
            suffix = stringResource(Res.string.suffix_percent)
        )
        StepperInputField(
            label = stringResource(Res.string.mortgage_years_label),
            value = state.mortgageYearsInput,
            onValueChange = { onIntent(HousePriceEvent.MortgageYearsChanged(it)) },
            onStep = { onIntent(HousePriceEvent.MortgageYearsStepped(it)) },
            suffix = stringResource(Res.string.suffix_years)
        )
        StepperInputField(
            label = stringResource(Res.string.cadastral_income_label),
            value = state.cadastralIncomeInput,
            onValueChange = { onIntent(HousePriceEvent.CadastralIncomeChanged(it)) },
            onStep = { onIntent(HousePriceEvent.CadastralIncomeStepped(it)) },
            suffix = stringResource(Res.string.suffix_eur)
        )
        StepperInputField(
            label = stringResource(Res.string.deposit_label),
            value = state.depositInput,
            onValueChange = { onIntent(HousePriceEvent.DepositChanged(it)) },
            onStep = { onIntent(HousePriceEvent.DepositStepped(it)) },
            suffix = stringResource(Res.string.suffix_eur)
        )
    }
}
