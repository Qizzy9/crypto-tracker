package com.qizzy9.cryptotracker.domain.model

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val marketCap: Long,
    val sparklineData: List<Double>,
    val isFavorite: Boolean = false,
)
