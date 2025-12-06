package com.example.campuswaldo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import com.example.campuswaldo.ui.theme.CampusWaldoTheme
import com.example.campuswaldo.ui.navigation.WaldoScreen
import com.example.campuswaldo.ui.screens.HuntRoute
import com.example.campuswaldo.ui.screens.LeaderboardRoute
import com.example.campuswaldo.ui.screens.WaldoOnlyScreen
import com.example.campuswaldo.ui.viewmodels.AppUiState
import com.example.campuswaldo.ui.viewmodels.UserRole
import com.example.campuswaldo.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusWaldoTheme {
                    CampusWaldoApp()

            }
        }
    }
}

@Composable
fun CampusWaldoApp(
    userViewModel: UserViewModel = viewModel()
) {
    val appUiState by userViewModel.appUiState.collectAsState()
    val navController = rememberNavController()

    val isWaldo = appUiState.role == UserRole.WALDO

    val tabs = if (isWaldo) {
        listOf(WaldoScreen.Waldo, WaldoScreen.Leaderboard)
    } else {
        listOf(WaldoScreen.Hunt, WaldoScreen.Leaderboard)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isWaldo) WaldoScreen.Waldo.route else WaldoScreen.Hunt.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(WaldoScreen.Hunt.route) { HuntRoute() }
            composable(WaldoScreen.Leaderboard.route) { LeaderboardRoute() }
            composable(WaldoScreen.Waldo.route) { WaldoOnlyScreen() }
        }
    }
}
