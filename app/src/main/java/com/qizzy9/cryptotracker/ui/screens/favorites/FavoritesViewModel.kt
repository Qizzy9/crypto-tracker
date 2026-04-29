package com.qizzy9.cryptotracker.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FavoritesUiState(val coins: List<Coin> = emptyList())

class FavoritesViewModel(
    repository: CoinRepository,
) : ViewModel() {

    val state = repository.getFavoriteCoins()
        .map { FavoritesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())
}
