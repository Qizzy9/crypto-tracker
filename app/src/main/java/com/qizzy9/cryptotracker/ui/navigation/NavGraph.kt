package com.qizzy9.cryptotracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qizzy9.cryptotracker.ui.screens.convert.ConvertScreen
import com.qizzy9.cryptotracker.ui.screens.detail.CoinDetailScreen
import com.qizzy9.cryptotracker.ui.screens.favorites.FavoritesScreen
import com.qizzy9.cryptotracker.ui.screens.list.CoinListScreen

private data class BottomNavItem(
    val label: String,
    val screen: Screen,
    val icon: @Composable () -> Unit,
)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem("Markets", Screen.CoinList) {
            Icon(Icons.Default.TrendingUp, contentDescription = null)
        },
        BottomNavItem("Convert", Screen.Convert) {
            Icon(Icons.Default.SwapHoriz, contentDescription = null)
        },
        BottomNavItem("Favorites", Screen.Favorites) {
            Icon(Icons.Default.Favorite, contentDescription = null)
        },
    )

    val bottomBarRoutes = listOf(Screen.CoinList.route, Screen.Convert.route, Screen.Favorites.route)
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.CoinList.route) {
            composable(Screen.CoinList.route) {
                CoinListScreen(onCoinClick = { navController.navigate(Screen.CoinDetail.createRoute(it)) }, paddingValues = padding)
            }
            composable(Screen.Convert.route) {
                ConvertScreen(paddingValues = padding)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onCoinClick = { navController.navigate(Screen.CoinDetail.createRoute(it)) }, paddingValues = padding)
            }
            composable(Screen.CoinDetail.route) { backStackEntry ->
                val coinId = backStackEntry.arguments?.getString("coinId") ?: return@composable
                CoinDetailScreen(coinId = coinId, onBack = { navController.popBackStack() })
            }
        }
    }
}
