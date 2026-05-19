package com.example.housebuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavDestination(
    val label: String,
    val icon: ImageVector
) {
    Calcolo(label = "Calcolo", icon = Icons.Default.Home),
    Schermo2(label = "Schermo 2", icon = Icons.AutoMirrored.Filled.List),
}
