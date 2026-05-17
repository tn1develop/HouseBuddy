package com.example.housepricescalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.housepricescalculator.presentation.mvi.HousePriceViewModel
import com.example.housepricescalculator.ui.screen.HousePriceScreen
import com.example.housepricescalculator.ui.theme.HousePricesCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HousePricesCalculatorTheme {
                val viewModel = remember { HousePriceViewModel() }
                HousePriceScreen(
                    state = viewModel.state,
                    result = viewModel.result,
                    onIntent = viewModel::handleEvent
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HousePricesCalculatorTheme {
        HousePriceScreen()
    }
}