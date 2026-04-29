package com.qizzy9.cryptotracker.ui.navigation

sealed class Screen(val route: String) {
    object CoinList : Screen("coin_list")
    object Favorites : Screen("favorites")
    object Convert : Screen("convert")
    object CoinDetail : Screen("coin_detail/{coinId}") {
        fun createRoute(coinId: String) = "coin_detail/$coinId"
    }
}
