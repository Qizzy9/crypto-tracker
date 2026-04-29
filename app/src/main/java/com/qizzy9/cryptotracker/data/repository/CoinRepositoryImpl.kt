package com.qizzy9.cryptotracker.data.repository

import com.qizzy9.cryptotracker.data.local.dao.CoinDao
import com.qizzy9.cryptotracker.data.local.entity.CoinEntity
import com.qizzy9.cryptotracker.data.remote.api.CoinGeckoApi
import com.qizzy9.cryptotracker.data.remote.dto.CoinDto
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import com.qizzy9.cryptotracker.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CoinRepositoryImpl(
    private val api: CoinGeckoApi,
    private val dao: CoinDao,
) : CoinRepository {

    override fun getCoins(): Flow<Resource<List<Coin>>> = flow {
        emit(Resource.Loading())

        val cached = dao.getCoins().first()
        if (cached.isNotEmpty()) {
            emit(Resource.Loading(cached.map { it.toDomain() }))
        }

        try {
            val remote = api.getCoins()
            dao.clearCoins()
            dao.insertCoins(remote.map { it.toEntity() })
            val fresh = dao.getCoins().first()
            emit(Resource.Success(fresh.map { it.toDomain() }))
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                emit(Resource.Success(cached.map { it.toDomain() }))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override fun getFavoriteCoins(): Flow<List<Coin>> =
        dao.getFavoriteCoins().map { it.map { entity -> entity.toDomain() } }

    override suspend fun toggleFavorite(coinId: String) {
        val isFav = dao.isFavorite(coinId)
        dao.updateFavorite(coinId, !isFav)
    }
}

private fun CoinDto.toEntity() = CoinEntity(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice ?: 0.0,
    priceChangePercentage24h = priceChangePercentage24h ?: 0.0,
    marketCap = marketCap ?: 0L,
    sparklineData = sparklineIn7d?.price?.joinToString(",") ?: "",
)

private fun CoinEntity.toDomain() = Coin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice,
    priceChangePercentage24h = priceChangePercentage24h,
    marketCap = marketCap,
    sparklineData = if (sparklineData.isEmpty()) emptyList()
    else sparklineData.split(",").mapNotNull { it.toDoubleOrNull() },
    isFavorite = isFavorite,
)
