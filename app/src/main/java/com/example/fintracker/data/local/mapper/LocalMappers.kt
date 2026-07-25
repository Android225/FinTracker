package com.example.fintracker.data.local.mapper

import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.CategoryEntity
import com.example.fintracker.data.local.entity.TransactionRecord
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import java.math.BigDecimal
import java.time.Instant

fun AccountEntity.toDomain(): Account = Account(
    id = localId,
    serverId = serverId,
    userId = userId ?: 0,
    name = name,
    emoji = emoji,
    balance = BigDecimal(balance),
    currency = currency,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis)
)

fun AccountEntity.toBrief(): AccountBrief = AccountBrief(
    id = localId,
    serverId = serverId,
    name = name,
    emoji = emoji,
    balance = BigDecimal(balance),
    currency = currency
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    emoji = emoji,
    isIncome = isIncome
)

fun TransactionRecord.toDomain(): Transaction = Transaction(
    id = transaction.localId,
    serverId = transaction.serverId,
    account = account.toBrief(),
    category = category.toDomain(),
    amount = BigDecimal(transaction.amount),
    transactionDate = Instant.ofEpochMilli(transaction.transactionDateEpochMillis),
    comment = transaction.comment,
    createdAt = Instant.ofEpochMilli(transaction.createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(transaction.updatedAtEpochMillis)
)
