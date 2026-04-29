package com.qizzy9.cryptotracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qizzy9.cryptotracker.data.local.dao.CoinDao
import com.qizzy9.cryptotracker.data.local.entity.CoinEntity

@Database(entities = [CoinEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
}
