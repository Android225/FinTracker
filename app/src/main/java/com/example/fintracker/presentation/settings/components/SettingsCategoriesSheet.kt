package com.example.fintracker.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import com.example.fintracker.domain.Category
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.list.ListItemContentText
import com.example.fintracker.presentation.common.components.list.ListItemLeadEmoji

const val CategoriesListTestTag = "categories_list"

@Composable
fun CategoriesSheet(
    categories: List<Category>,
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        SettingsSheetTitle(stringResource(R.string.settings_categories))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.category_search_hint)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.settings_search),
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(32.dp),
            singleLine = true
        )
        if (categories.isEmpty()) {
            Text(
                text = stringResource(
                    if (query.isBlank()) R.string.categories_empty
                    else R.string.categories_search_empty
                ),
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag(CategoriesListTestTag),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    ListItem(
                        lead = { ListItemLeadEmoji(category.emoji) },
                        content = { ListItemContentText(category.name) },
                        trail = {},
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
                    )
                }
            }
        }
    }
}
