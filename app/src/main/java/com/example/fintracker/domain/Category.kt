package com.example.fintracker.domain

// Категория дохода или расхода.
data class Category(
    val id: Int,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)
