package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Category
import com.example.fintracker.domain.result.DataResult

interface CategoriesRepository {
    suspend fun getCategories(): DataResult<List<Category>>
}
