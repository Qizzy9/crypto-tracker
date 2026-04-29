package com.qizzy9.cryptotracker.ui.screens.convert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import com.qizzy9.cryptotracker.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConvertUiState(
    val coins: List<Coin> = emptyList(),
    val fromCoin: Coin? = null,
    val toCoin: Coin? = null,
    val inputAmount: String = "",
    val result: Double? = null,
    val isLoading: Boolean = true,
)

class ConvertViewModel(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ConvertUiState())
    val state = _state.asStateFlow()

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            repository.getCoins().collect { resource ->
                val coins = when (resource) {
                    is Resource.Success -> resource.data
                    is Resource.Loading -> resource.data ?: emptyList()
                    is Resource.Error -> resource.data ?: emptyList()
                }
                if (coins.isNotEmpty()) {
                    val btc = coins.find { it.id == "bitcoin" } ?: coins[0]
                    val usdt = coins.find { it.id == "tether" } ?: coins.getOrNull(1) ?: coins[0]
                    _state.update {
                        it.copy(
                            coins = coins,
                            fromCoin = it.fromCoin ?: btc,
                            toCoin = it.toCoin ?: usdt,
                            isLoading = false,
                        )
                    }
                    recalculate()
                }
            }
        }
    }

    fun onInputChange(value: String) {
        _state.update { it.copy(inputAmount = value) }
        recalculate()
    }

    fun onFromCoinSelected(coin: Coin) {
        _state.update { it.copy(fromCoin = coin) }
        recalculate()
    }

    fun onToCoinSelected(coin: Coin) {
        _state.update { it.copy(toCoin = coin) }
        recalculate()
    }

    fun swapCoins() {
        _state.update { it.copy(fromCoin = it.toCoin, toCoin = it.fromCoin) }
        recalculate()
    }

    private fun recalculate() {
        val s = _state.value
        val amount = s.inputAmount.replace(",", ".").toDoubleOrNull()
        val from = s.fromCoin?.currentPrice ?: return
        val to = s.toCoin?.currentPrice?.takeIf { it > 0 } ?: return
        _state.update { it.copy(result = if (amount != null) amount * from / to else null) }
    }
}
