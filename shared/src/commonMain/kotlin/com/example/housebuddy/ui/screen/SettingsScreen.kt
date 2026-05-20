package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
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
                checked = state.mutuoGreen,
                onCheckedChange = { onIntent(HousePriceEvent.MutuoGreenChanged(it)) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Percentuale o fisso (Agenzia)")
            Switch(
                checked = state.isPercentuale,
                onCheckedChange = { onIntent(HousePriceEvent.IsPercentualeChanged(it)) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Totale o Pro Capite")
            Switch(
                checked = state.isTotalExpenses,
                onCheckedChange = { onIntent(HousePriceEvent.IsProCapiteChanged(it)) }
            )
        }
        StepperInputField(
            label = "Tasso mutuo",
            value = state.tassoMutuoInput,
            onValueChange = { onIntent(HousePriceEvent.TassoMutuoChanged(it)) },
            onStep = { onIntent(HousePriceEvent.TassoMutuoStepped(it)) },
            suffix = "%"
        )
        StepperInputField(
            label = "Anni mutuo",
            value = state.anniMutuoInput,
            onValueChange = { onIntent(HousePriceEvent.AnniMutuoChanged(it)) },
            onStep = { onIntent(HousePriceEvent.AnniMutuoStepped(it)) },
            suffix = "anni"
        )
        StepperInputField(
            label = "Rendita catastale",
            value = state.renditaCatastaleInput,
            onValueChange = { onIntent(HousePriceEvent.RenditaCatastaleChanged(it)) },
            onStep = { onIntent(HousePriceEvent.RenditaCatastaleStepped(it)) },
            suffix = "EUR"
        )
    }
}
