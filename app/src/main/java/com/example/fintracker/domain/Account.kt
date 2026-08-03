package com.example.fintracker.domain

import java.math.BigDecimal
import java.time.Instant

data class Account(
    val id: Long,
    val serverId: Int?,
    val userId: Int,
    val name: String,
    val emoji: String,
    val balance: BigDecimal,
    val currency: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val syncPending: Boolean = false
)

data class AccountBrief(
    val id: Long,
    val serverId: Int?,
    val name: String,
    val emoji: String,
    val balance: BigDecimal,
    val currency: String
)

data class AccountDraft(
    val name: String,
    val emoji: String,
    val balance: BigDecimal,
    val currency: String
)
