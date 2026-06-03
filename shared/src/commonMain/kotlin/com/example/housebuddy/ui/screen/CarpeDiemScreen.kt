package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.StepperInputField

@Composable
fun CarpeDiemScreen(
    state: HousePriceViewState,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
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
        }
    }
}
