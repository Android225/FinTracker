package com.example.fintracker.data.remote.api

import com.example.fintracker.data.remote.dto.AccountDto
import com.example.fintracker.data.remote.dto.CategoryDto
import com.example.fintracker.data.remote.dto.TransactionResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinanceApi {
    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsForPeriod(
        @Path("accountId") accountId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionResponseDto>
}
