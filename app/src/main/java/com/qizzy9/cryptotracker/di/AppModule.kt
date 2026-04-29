package com.qizzy9.cryptotracker.di

import androidx.room.Room
import com.qizzy9.cryptotracker.data.local.AppDatabase
import com.qizzy9.cryptotracker.data.remote.api.CoinGeckoApi
import com.qizzy9.cryptotracker.data.repository.CoinRepositoryImpl
import com.qizzy9.cryptotracker.domain.repository.CoinRepository
import com.qizzy9.cryptotracker.ui.screens.convert.ConvertViewModel
import com.qizzy9.cryptotracker.ui.screens.detail.CoinDetailViewModel
import com.qizzy9.cryptotracker.ui.screens.favorites.FavoritesViewModel
import com.qizzy9.cryptotracker.ui.screens.list.CoinListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(CoinGeckoApi.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(CoinGeckoApi::class.java) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "crypto_db").build()
    }

    single { get<AppDatabase>().coinDao() }

    single<CoinRepository> { CoinRepositoryImpl(get(), get()) }

    viewModel { CoinListViewModel(get()) }
    viewModel { CoinDetailViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { ConvertViewModel(get()) }
}
