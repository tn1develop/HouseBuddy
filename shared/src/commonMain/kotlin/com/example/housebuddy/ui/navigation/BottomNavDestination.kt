package com.example.housebuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Calcolo(route = "calcolo", label = "Calcolo", icon = Icons.Default.Home),
    AndamentoStorico(route = "storico", label = "Storico", icon = Icons.AutoMirrored.Filled.List),
    Trasferimento(route = "trasferimento", label = "Trasferimento", icon = Icons.Default.SwapHoriz),
}
