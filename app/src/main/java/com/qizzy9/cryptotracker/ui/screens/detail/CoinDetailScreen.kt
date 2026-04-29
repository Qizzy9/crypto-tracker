package com.qizzy9.cryptotracker.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.ui.components.SparklineChart
import com.qizzy9.cryptotracker.ui.theme.PriceGreen
import com.qizzy9.cryptotracker.ui.theme.PriceRed
import com.qizzy9.cryptotracker.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    onBack: () -> Unit,
    viewModel: CoinDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(coinId) { viewModel.loadCoin(coinId) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.coin?.name ?: "") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = viewModel::toggleFavorite) {
                    Icon(
                        imageVector = if (state.coin?.isFavorite == true) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (state.coin?.isFavorite == true) PriceRed
                        else MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            state.coin != null -> CoinDetailContent(coin = state.coin!!)
            state.error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CoinDetailContent(coin: Coin) {
    val priceColor = if (coin.priceChangePercentage24h >= 0) PriceGreen else PriceRed
    val changePrefix = if (coin.priceChangePercentage24h >= 0) "+" else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier.size(48.dp),
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = coin.symbol.uppercase(),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = formatPrice(coin.currentPrice),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
        )

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = priceColor.copy(alpha = 0.15f),
            modifier = Modifier.padding(top = 6.dp),
        ) {
            Text(
                text = "$changePrefix${"%.2f".format(coin.priceChangePercentage24h)}%  24h",
                color = priceColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(24.dp))

        if (coin.sparklineData.size >= 2) {
            Text(
                text = "7-day price",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            SparklineChart(
                data = coin.sparklineData,
                lineColor = priceColor,
                showGradient = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            )
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Market Info", fontWeight = FontWeight.SemiBold)
                StatRow(label = "Market Cap", value = formatMarketCap(coin.marketCap))
                StatRow(label = "Symbol", value = coin.symbol.uppercase())
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatPrice(price: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US)
    fmt.maximumFractionDigits = if (price < 1) 4 else 2
    return fmt.format(price)
}

private fun formatMarketCap(cap: Long): String {
    return when {
        cap >= 1_000_000_000 -> "${"%.2f".format(cap / 1_000_000_000.0)}B"
        cap >= 1_000_000 -> "${"%.2f".format(cap / 1_000_000.0)}M"
        else -> cap.toString()
    }
}
