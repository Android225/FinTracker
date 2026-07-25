package com.example.fintracker.domain

import java.math.BigDecimal
import java.time.Instant

data class Transaction(
    val id: Long,
    val serverId: Int?,
    val account: AccountBrief,
    val category: Category,
    val amount: BigDecimal,
    val transactionDate: Instant,
    val comment: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class TransactionDraft(
    val accountId: Long,
    val categoryId: Int,
    val amount: BigDecimal,
    val transactionDate: Instant,
    val comment: String?
)
