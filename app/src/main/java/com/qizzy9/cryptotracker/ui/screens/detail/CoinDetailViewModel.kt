package com.qizzy9.cryptotracker.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import com.qizzy9.cryptotracker.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CoinDetailUiState(
    val coin: Coin? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class CoinDetailViewModel(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinDetailUiState())
    val state = _state.asStateFlow()

    fun loadCoin(coinId: String) {
        viewModelScope.launch {
            repository.getCoins().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        val coin = result.data?.find { it.id == coinId }
                        _state.update { it.copy(isLoading = coin == null, coin = coin ?: it.coin) }
                    }
                    is Resource.Success -> {
                        val coin = result.data.find { it.id == coinId }
                        _state.update { it.copy(isLoading = false, coin = coin, error = null) }
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val coinId = _state.value.coin?.id ?: return
        viewModelScope.launch {
            repository.toggleFavorite(coinId)
            _state.update { s ->
                s.copy(coin = s.coin?.copy(isFavorite = !s.coin.isFavorite))
            }
        }
    }
}
