package com.qizzy9.cryptotracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qizzy9.cryptotracker.data.local.entity.CoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("SELECT * FROM coins ORDER BY marketCap DESC")
    fun getCoins(): Flow<List<CoinEntity>>

    @Query("SELECT * FROM coins WHERE isFavorite = 1 ORDER BY marketCap DESC")
    fun getFavoriteCoins(): Flow<List<CoinEntity>>

    @Query("SELECT isFavorite FROM coins WHERE id = :coinId")
    suspend fun isFavorite(coinId: String): Boolean

    @Query("UPDATE coins SET isFavorite = :isFavorite WHERE id = :coinId")
    suspend fun updateFavorite(coinId: String, isFavorite: Boolean)

    @Query("DELETE FROM coins")
    suspend fun clearCoins()
}
