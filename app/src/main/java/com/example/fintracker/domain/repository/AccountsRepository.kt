package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.result.DataResult

interface AccountsRepository {
    suspend fun getAccounts(): DataResult<List<Account>>
}
