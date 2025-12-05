package com.example.campuswaldo.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WaldoScreen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Hunt : WaldoScreen("hunt", "Hunt", Icons.Filled.Home)
    object Leaderboard : WaldoScreen("leaderboard", "Leaderboard", Icons.Filled.List)
    object Waldo : WaldoScreen("waldo", "Waldo", Icons.Filled.Person)
}
