package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsAccountOption

// Bottom sheet выбора счёта.
@Composable
fun AnalyticsAccountBottomSheet(
    options: List<AnalyticsAccountOption>,
    onSelect: (AnalyticsAccountOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(448.dp)
    ) {
        AnalyticsSheetHeader(title = "Счёт")
        LazyColumn {
            itemsIndexed(
                items = options,
                key = { _, option -> option.id ?: Int.MIN_VALUE }
            ) { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (option.subtitle == null) 64.dp else 69.dp)
                        .clickable { onSelect(option) }
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnalyticsEmojiIcon(emoji = option.emoji)
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = option.title,
                            fontSize = 16.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        option.subtitle?.let { subtitle ->
                            Text(
                                text = subtitle,
                                modifier = Modifier.padding(top = 2.dp),
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    AnalyticsCheckmark(visible = option.selected)
                }
                if (index != options.lastIndex) AnalyticsSheetDivider()
            }
        }
    }
}
