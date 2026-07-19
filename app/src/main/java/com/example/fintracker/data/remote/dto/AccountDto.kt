package com.example.fintracker.data.remote.dto

data class AccountDto(
    val id: Int?,
    val userId: Int?,
    val name: String?,
    val emoji: String?,
    val balance: String?,
    val currency: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class AccountBriefDto(
    val id: Int?,
    val name: String?,
    val emoji: String?,
    val balance: String?,
    val currency: String?
)
