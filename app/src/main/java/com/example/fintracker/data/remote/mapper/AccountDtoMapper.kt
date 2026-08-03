package com.example.fintracker.data.remote.mapper

import com.example.fintracker.data.remote.dto.AccountBriefDto
import com.example.fintracker.data.remote.dto.AccountDto
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountBrief

internal fun AccountDto.toDomain(): Account = Account(
    id = id.requireRemoteField("account.id").toLong(),
    serverId = id,
    userId = userId.requireRemoteField("account.userId"),
    name = name.requireRemoteField("account.name"),
    emoji = emoji.requireRemoteField("account.emoji"),
    balance = balance.requireRemoteField("account.balance").toRemoteBigDecimal("account.balance"),
    currency = currency.requireRemoteField("account.currency"),
    createdAt = createdAt.requireRemoteField("account.createdAt").toRemoteInstant("account.createdAt"),
    updatedAt = updatedAt.requireRemoteField("account.updatedAt").toRemoteInstant("account.updatedAt")
)

internal fun AccountBriefDto.toDomain(): AccountBrief = AccountBrief(
    id = id.requireRemoteField("account.id").toLong(),
    serverId = id,
    name = name.requireRemoteField("account.name"),
    emoji = emoji.requireRemoteField("account.emoji"),
    balance = balance.requireRemoteField("account.balance").toRemoteBigDecimal("account.balance"),
    currency = currency.requireRemoteField("account.currency")
)
