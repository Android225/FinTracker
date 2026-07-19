package com.example.fintracker.data.repository

import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.data.remote.mapper.toDomain
import com.example.fintracker.data.remote.util.executeRemoteCall
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val api: FinanceApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {
    override suspend fun getAccounts(): DataResult<List<Account>> = withContext(ioDispatcher) {
        executeRemoteCall { api.getAccounts().map { it.toDomain() } }
    }
}
