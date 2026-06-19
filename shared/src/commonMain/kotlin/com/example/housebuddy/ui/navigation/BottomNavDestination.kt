package com.example.housebuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.nav_calcolo
import housebuddy.shared.generated.resources.nav_menu
import housebuddy.shared.generated.resources.nav_storico
import org.jetbrains.compose.resources.StringResource

enum class BottomNavDestination(
    val route: String,
    val labelRes: StringResource,
    val icon: ImageVector
) {
    Calcolo(route = "calcolo", labelRes = Res.string.nav_calcolo, icon = Icons.Default.Home),
    AndamentoStorico(route = "storico", labelRes = Res.string.nav_storico, icon = Icons.AutoMirrored.Filled.TrendingUp),
    Menu(route = "menu", labelRes = Res.string.nav_menu, icon = Icons.Default.Menu),
}
