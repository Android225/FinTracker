package com.example.fintracker.domain

data class Account(
    val id: Int,
    val userId: Int,
    val name: String,
    val balance: String,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)

data class AccountCreateRequest(
    val name: String,
    val balance: String,
    val currency: String
)

data class AccountUpdateRequest(
    val name: String,
    val balance: String,
    val currency: String
)

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

data class AccountBrief(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)

data class AccountState(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)

data class StatItem(
    val categoryId: Int,
    val categoryName: String,
    val emoji: String,
    val amount: String
)