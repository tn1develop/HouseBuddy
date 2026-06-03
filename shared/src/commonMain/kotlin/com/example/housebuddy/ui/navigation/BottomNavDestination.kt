package com.example.housebuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Calcolo(route = "calcolo", label = "Calcolo", icon = Icons.Default.Home),
    AndamentoStorico(route = "storico", label = "Storico", icon = Icons.AutoMirrored.Filled.TrendingUp ),
    Menu(route = "menu", label = "Menu", icon = Icons.Default.Menu),
}
