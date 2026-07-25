package com.example.fintracker.data.remote.dto

data class TransactionMutationRequestDto(
    val accountId: Int,
    val categoryId: Int,
    val amount: String,
    val transactionDate: String,
    val comment: String?
)

data class TransactionMutationResponseDto(
    val id: Int?,
    val accountId: Int?,
    val categoryId: Int?,
    val amount: String?,
    val transactionDate: String?,
    val comment: String?,
    val createdAt: String?,
    val updatedAt: String?
)
