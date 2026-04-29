package com.qizzy9.cryptotracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoinDto(
    @SerializedName("id") val id: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("current_price") val currentPrice: Double?,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double?,
    @SerializedName("market_cap") val marketCap: Long?,
    @SerializedName("sparkline_in_7d") val sparklineIn7d: SparklineDto?,
)

data class SparklineDto(
    @SerializedName("price") val price: List<Double>?,
)
