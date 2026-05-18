package com.example.housebuddy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.housebuddy.presentation.mvi.HousePriceViewModel
import com.example.housebuddy.ui.screen.HousePriceScreen
import com.example.housebuddy.ui.theme.HousePricesCalculatorTheme

@Composable
fun App() {
    HousePricesCalculatorTheme {
        val viewModel = remember { HousePriceViewModel() }
        HousePriceScreen(
            state = viewModel.state,
            result = viewModel.result,
            onIntent = viewModel::handleEvent
        )
    }
}
