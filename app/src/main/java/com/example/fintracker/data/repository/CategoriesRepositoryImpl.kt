package com.example.fintracker.data.repository

import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.data.remote.mapper.toDomain
import com.example.fintracker.data.remote.util.executeRemoteCall
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val api: FinanceApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CategoriesRepository {
    override suspend fun getCategories(): DataResult<List<Category>> = withContext(ioDispatcher) {
        executeRemoteCall { api.getCategories().map { it.toDomain() } }
    }

    override suspend fun getCategoriesByType(
        isIncome: Boolean
    ): DataResult<List<Category>> = withContext(ioDispatcher) {
        executeRemoteCall { api.getCategoriesByType(isIncome).map { it.toDomain() } }
    }
}
