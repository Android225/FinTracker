package com.example.fintracker.domain

import java.math.BigDecimal
import java.time.Instant

data class Account(
    val id: Int,
    val userId: Int,
    val name: String,
    val emoji: String,
    val balance: BigDecimal,
    val currency: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class AccountBrief(
    val id: Int,
    val name: String,
    val emoji: String,
    val balance: BigDecimal,
    val currency: String
)
