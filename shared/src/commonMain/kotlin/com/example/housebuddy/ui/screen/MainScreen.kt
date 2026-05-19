package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.housebuddy.presentation.mvi.HousePriceViewModel
import com.example.housebuddy.ui.navigation.BottomNavDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HousePriceViewModel = remember { HousePriceViewModel() }
) {
    var destination by remember { mutableStateOf(BottomNavDestination.Calcolo) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = destination.label) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Impostazioni"
                        )
                    }
                }
            )
        },
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
