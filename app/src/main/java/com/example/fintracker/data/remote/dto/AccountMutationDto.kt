package com.example.fintracker.data.remote.dto

data class AccountMutationRequestDto(
    val name: String,
    val emoji: String?,
    val balance: String,
    val currency: String
)
