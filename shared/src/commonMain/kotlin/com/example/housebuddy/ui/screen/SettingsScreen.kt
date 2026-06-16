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

@Composable
fun SettingsScreen(
    state: HousePriceViewState,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(text = "Mutuo Green (classe A o B)")
            Switch(
                checked = state.greenMortgage,
                onCheckedChange = { onIntent(HousePriceEvent.GreenMortgageChanged(it)) }
            )
        }
        MyDropdown(
            label = "Provvigione Agenzia",
            options = agencyCommissionOptions,
            selectedOption = if (state.isAgencyCommissionPercentage) "Percentuale" else "Importo Fisso",
            onOptionSelected = { onIntent(HousePriceEvent.AgencyCommissionTypeChanged(it == "Percentuale")) },
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
                text = "N. Acquirenti",
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
            label = "Tasso mutuo",
            value = state.mortgageRateInput,
            onValueChange = { onIntent(HousePriceEvent.MortgageRateChanged(it)) },
            onStep = { onIntent(HousePriceEvent.MortgageRateStepped(it)) },
            suffix = "%"
        )
        StepperInputField(
            label = "Anni mutuo",
            value = state.mortgageYearsInput,
            onValueChange = { onIntent(HousePriceEvent.MortgageYearsChanged(it)) },
            onStep = { onIntent(HousePriceEvent.MortgageYearsStepped(it)) },
            suffix = "anni"
        )
        StepperInputField(
            label = "Rendita catastale",
            value = state.cadastralIncomeInput,
            onValueChange = { onIntent(HousePriceEvent.CadastralIncomeChanged(it)) },
            onStep = { onIntent(HousePriceEvent.CadastralIncomeStepped(it)) },
            suffix = "EUR"
        )
        StepperInputField(
            label = "Caparra",
            value = state.depositInput,
            onValueChange = { onIntent(HousePriceEvent.DepositChanged(it)) },
            onStep = { onIntent(HousePriceEvent.DepositStepped(it)) },
            suffix = "EUR"
        )
    }
}

private val agencyCommissionOptions = listOf("Percentuale", "Importo Fisso")
