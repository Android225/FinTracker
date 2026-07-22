package com.example.fintracker.presentation.screens.analytics.components.filters

fun formatCategoriesForDisplay(categories: String): String {
    val items = categories.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    return when {
        items.isEmpty() -> "Все категории"
        items.size == 1 -> items[0]
        items.size == 2 -> items.joinToString(", ")
        else -> "${items[0]}, ${items[1]}…" // Многоточие если есть еще категории
    }
}