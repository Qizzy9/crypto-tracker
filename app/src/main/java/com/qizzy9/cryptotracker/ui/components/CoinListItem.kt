package com.qizzy9.cryptotracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.qizzy9.cryptotracker.domain.model.Coin
import com.qizzy9.cryptotracker.ui.theme.PriceGreen
import com.qizzy9.cryptotracker.ui.theme.PriceRed
import com.qizzy9.cryptotracker.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CoinListItem(
    coin: Coin,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val priceColor = if (coin.priceChangePercentage24h >= 0) PriceGreen else PriceRed
    val changePrefix = if (coin.priceChangePercentage24h >= 0) "+" else ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = coin.image,
            contentDescription = coin.name,
            modifier = Modifier.size(44.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = coin.symbol.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        if (coin.sparklineData.size >= 2) {
            SparklineChart(
                data = coin.sparklineData,
                lineColor = priceColor,
                showGradient = false,
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp),
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            Text(
                text = formatPrice(coin.currentPrice),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = "$changePrefix${"%.2f".format(coin.priceChangePercentage24h)}%",
                style = MaterialTheme.typography.bodySmall,
                color = priceColor,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private fun formatPrice(price: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US)
    fmt.maximumFractionDigits = if (price < 1) 4 else 2
    return fmt.format(price)
}
