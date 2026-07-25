package com.example.fintracker.data.repository

import com.example.fintracker.data.local.dao.CategoryDao
import com.example.fintracker.data.local.entity.CategoryEntity
import com.example.fintracker.data.local.mapper.toDomain
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CategoriesRepository {
    override suspend fun getCategories(): DataResult<List<Category>> = withContext(ioDispatcher) {
        getLocal { categoryDao.getAll() }
    }

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { categories -> categories.map(CategoryEntity::toDomain) }

    override suspend fun getCategories(
        isIncome: Boolean
    ): DataResult<List<Category>> = withContext(ioDispatcher) {
        getLocal { categoryDao.getByType(isIncome) }
    }

    private suspend fun getLocal(
        block: suspend () -> List<CategoryEntity>
    ): DataResult<List<Category>> = try {
        DataResult.Success(block().map(CategoryEntity::toDomain))
    } catch (exception: Exception) {
        DataResult.Failure(AppError.Unknown(exception.message))
    }
}
