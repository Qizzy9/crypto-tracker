package com.qizzy9.cryptotracker.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import com.qizzy9.cryptotracker.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CoinListUiState(
    val coins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
)

class CoinListViewModel(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListUiState())
    private val _searchQuery = MutableStateFlow("")

    val state = combine(_state, _searchQuery) { state, query ->
        state.copy(
            searchQuery = query,
            coins = if (query.isBlank()) state.coins
            else state.coins.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.symbol.contains(query, ignoreCase = true)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CoinListUiState())

    init {
        loadCoins()
    }

    fun onSearchQueryChange(query: String) = _searchQuery.update { query }

    fun refresh() = loadCoins()

    private fun loadCoins() {
        viewModelScope.launch {
            repository.getCoins().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update {
                        it.copy(isLoading = result.data == null, coins = result.data ?: it.coins)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, coins = result.data, error = null)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}
