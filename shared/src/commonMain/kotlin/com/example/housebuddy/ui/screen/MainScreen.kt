package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.housebuddy.presentation.mvi.HousePriceViewModel
import com.example.housebuddy.ui.navigation.BottomNavDestination

@Composable
fun MainScreen(
    viewModel: HousePriceViewModel = remember { HousePriceViewModel() }
) {
    var destination by remember { mutableStateOf(BottomNavDestination.Calcolo) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavDestination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = destination == item,
                        onClick = { destination = item },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (destination) {
            BottomNavDestination.Calcolo -> HousePriceScreen(
                state = viewModel.state,
                result = viewModel.result,
                onIntent = viewModel::handleEvent,
                modifier = Modifier.padding(innerPadding)
            )
            BottomNavDestination.Schermo2 -> PlaceholderScreen(
                title = "Schermo 2",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
