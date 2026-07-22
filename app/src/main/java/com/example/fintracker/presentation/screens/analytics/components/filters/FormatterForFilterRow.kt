package com.example.fintracker.presentation.screens.analytics.components.filters

import android.content.Context
import com.example.fintracker.R

fun formatCategoriesForDisplay(context: Context, categories: String): String {
    val items = categories.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    return when {
        items.isEmpty() -> context.getString(R.string.all_categories)
        items.size == 1 -> items[0]
        items.size == 2 -> items.joinToString(", ")
        else -> "${items[0]}, ${items[1]}…" // Многоточие если есть еще категории
    }
}