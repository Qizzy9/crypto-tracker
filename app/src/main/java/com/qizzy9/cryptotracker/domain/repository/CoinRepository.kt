package com.qizzy9.cryptotracker.domain.repository

import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getCoins(): Flow<Resource<List<Coin>>>
    fun getFavoriteCoins(): Flow<List<Coin>>
    suspend fun toggleFavorite(coinId: String)
}
