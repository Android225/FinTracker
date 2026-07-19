package com.example.fintracker.data.remote.mapper

import com.example.fintracker.data.remote.dto.TransactionResponseDto
import com.example.fintracker.domain.Transaction

internal fun TransactionResponseDto.toDomain(): Transaction = Transaction(
    id = id.requireRemoteField("transaction.id"),
    account = account.requireRemoteField("transaction.account").toDomain(),
    category = category.requireRemoteField("transaction.category").toDomain(),
    amount = amount.requireRemoteField("transaction.amount")
        .toRemoteBigDecimal("transaction.amount"),
    transactionDate = transactionDate.requireRemoteField("transaction.transactionDate")
        .toRemoteInstant("transaction.transactionDate"),
    comment = comment,
    createdAt = createdAt.requireRemoteField("transaction.createdAt")
        .toRemoteInstant("transaction.createdAt"),
    updatedAt = updatedAt.requireRemoteField("transaction.updatedAt")
        .toRemoteInstant("transaction.updatedAt")
)
