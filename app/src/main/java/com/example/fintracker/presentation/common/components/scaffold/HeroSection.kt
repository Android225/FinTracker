package com.example.fintracker.presentation.common.components.scaffold

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fintracker.presentation.common.format.MoneyTotalUi

@Composable
fun HeroSection(
    label: String,
    amounts: List<MoneyTotalUi>
) {
    val availableAmounts = amounts.ifEmpty {
        listOf(MoneyTotalUi(currency = "RUB", formattedAmount = "0 ₽"))
    }
    var selectedCurrency by remember(availableAmounts.map(MoneyTotalUi::currency)) {
        mutableStateOf(availableAmounts.first().currency)
    }
    val selectedAmount = availableAmounts.firstOrNull {
        it.currency == selectedCurrency
    } ?: availableAmounts.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        if (availableAmounts.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableAmounts.forEach { amount ->
                    FilterChip(
                        selected = amount.currency == selectedAmount.currency,
                        onClick = { selectedCurrency = amount.currency },
                        label = { Text(amount.currency) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(if (availableAmounts.size > 1) 8.dp else 4.dp))
        Text(
            text = selectedAmount.formattedAmount,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
