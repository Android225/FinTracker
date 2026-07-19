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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsCategoryOption

// Bottom sheet множественного выбора категорий.
@Composable
fun AnalyticsCategoriesBottomSheet(
    options: List<AnalyticsCategoryOption>,
    onApply: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIds by remember(options) {
        mutableStateOf(options.filter(AnalyticsCategoryOption::selected).map { it.id }.toSet())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(648.dp)
    ) {
        AnalyticsSheetHeader(title = "Статьи")
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(
                items = options,
                key = { _, option -> option.id }
            ) { index, option ->
                val toggle = {
                    selectedIds = if (option.id in selectedIds) {
                        selectedIds - option.id
                    } else {
                        selectedIds + option.id
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable(onClick = toggle)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnalyticsEmojiIcon(emoji = option.emoji)
                    Text(
                        text = option.title,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AnalyticsCheckbox(
                        checked = option.id in selectedIds,
                        onClick = toggle
                    )
                }
                if (index != options.lastIndex) AnalyticsSheetDivider()
            }
        }
        AnalyticsSheetButton(
            text = "Применить",
            onClick = { onApply(selectedIds) },
            height = 48.dp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 40.dp)
        )
    }
}
