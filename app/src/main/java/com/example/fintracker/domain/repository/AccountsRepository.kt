package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    suspend fun getAccounts(): DataResult<List<Account>>
    fun observeAccounts(): Flow<List<Account>>
    suspend fun getAccount(id: Long): DataResult<Account>
    suspend fun saveAccount(id: Long?, draft: AccountDraft): DataResult<Long>
    suspend fun deleteAccount(id: Long): DataResult<Unit>
}
