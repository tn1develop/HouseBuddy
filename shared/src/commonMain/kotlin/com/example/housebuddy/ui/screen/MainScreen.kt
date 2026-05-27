package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.housebuddy.presentation.mvi.ExchangeRateViewModel
import com.example.housebuddy.presentation.mvi.HousePriceViewModel
import com.example.housebuddy.ui.navigation.AppDestination
import com.example.housebuddy.ui.navigation.BottomNavDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HousePriceViewModel = remember { HousePriceViewModel() },
    exchangeRateViewModel: ExchangeRateViewModel = remember { ExchangeRateViewModel() }
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isSettingsRoute = currentDestination?.route == AppDestination.Settings
    val showBottomBar = currentDestination?.isBottomDestination() == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = currentDestination.resolveTopBarTitle()) },
                navigationIcon = {
                    if (currentDestination?.isTopLevelDestination() == false) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro"
                            )
                        }
                    }
                },
                actions = {
                    if (!isSettingsRoute) {
                        IconButton(onClick = { navController.navigate(AppDestination.Settings) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Impostazioni"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavDestination.entries.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination.isOnDestination(item),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.Calcolo.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavDestination.Calcolo.route) {
                HousePriceScreen(
                    state = viewModel.state,
                    result = viewModel.result,
                    onIntent = viewModel::handleEvent
                )
            }
            composable(BottomNavDestination.AndamentoStorico.route) {
                ExchangeRateScreen(
                    state = exchangeRateViewModel.state,
                    onIntent = exchangeRateViewModel::handleEvent
                )
            }
            composable(BottomNavDestination.Trasferimento.route) {
                TransferScreen(
                    onReceiveClick = { navController.navigate(AppDestination.TransferReceive) },
                    onSendClick = { navController.navigate(AppDestination.TransferSend) }
                )
            }
            composable(AppDestination.TransferReceive) {
                ReceiveTransferScreen(
                    onChangeDirection = { navController.popBackStack() }
                )
            }
            composable(AppDestination.TransferSend) {
                SendTransferScreen(
                    onChangeDirection = { navController.popBackStack() }
                )
            }
            composable(AppDestination.Settings) {
                SettingsScreen(
                    state = viewModel.state,
                    onIntent = viewModel::handleEvent
                )
            }
        }
    }
}

private fun NavDestination?.isOnDestination(item: BottomNavDestination): Boolean {
    if (this == null) return false
    return hierarchy.any { destination ->
        when (item) {
            BottomNavDestination.Trasferimento ->
                destination.route == item.route ||
                    destination.route == AppDestination.TransferReceive ||
                    destination.route == AppDestination.TransferSend

            else -> destination.route == item.route
        }
    }
}

private fun NavDestination?.isBottomDestination(): Boolean {
    if (this == null) return false
    return BottomNavDestination.entries.any { isOnDestination(it) }
}

private fun NavDestination?.isTopLevelDestination(): Boolean {
    if (this == null) return true
    return BottomNavDestination.entries.any { item -> route == item.route }
}

private fun NavDestination?.resolveTopBarTitle(): String {
    return when (this?.route) {
        AppDestination.Settings -> "Impostazioni"
        AppDestination.TransferReceive -> "Ricevi trasferimento"
        AppDestination.TransferSend -> "Invia trasferimento"
        BottomNavDestination.AndamentoStorico.route -> BottomNavDestination.AndamentoStorico.label
        BottomNavDestination.Trasferimento.route -> BottomNavDestination.Trasferimento.label
        else -> BottomNavDestination.Calcolo.label
    }
}
