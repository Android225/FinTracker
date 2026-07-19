package com.example.fintracker.domain

// Полная сущность банковского счёта.
data class Account(
    val id: Int,
    val userId: Int,
    val name: String,
    val balance: String,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)

// Данные для создания нового счёта.
data class AccountCreateRequest(
    val name: String,
    val balance: String,
    val currency: String
)

// Данные для изменения существующего счёта.
data class AccountUpdateRequest(
    val name: String,
    val balance: String,
    val currency: String
)

// Расширенная информация о счёте со статистикой.
data class AccountResponse(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String,
    val incomeStats: List<StatItem>,
    val expenseStats: List<StatItem>,
    val createdAt: String,
    val updatedAt: String
)

// Краткая информация о счёте для вложенных моделей.
data class AccountBrief(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)

// Текущее состояние счёта без служебных полей.
data class AccountState(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)

// Сумма операций по отдельной категории.
data class StatItem(
    val categoryId: Int,
    val categoryName: String,
    val emoji: String,
    val amount: String
)
