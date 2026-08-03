package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Category
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    suspend fun getCategories(): DataResult<List<Category>>
    fun observeCategories(): Flow<List<Category>>
    suspend fun getCategories(isIncome: Boolean): DataResult<List<Category>>
}
