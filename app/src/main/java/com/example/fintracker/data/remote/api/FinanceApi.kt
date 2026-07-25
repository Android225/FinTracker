package com.example.fintracker.data.remote.api

import com.example.fintracker.data.remote.dto.AccountDto
import com.example.fintracker.data.remote.dto.AccountMutationRequestDto
import com.example.fintracker.data.remote.dto.CategoryDto
import com.example.fintracker.data.remote.dto.TransactionMutationRequestDto
import com.example.fintracker.data.remote.dto.TransactionMutationResponseDto
import com.example.fintracker.data.remote.dto.TransactionResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FinanceApi {
    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    @POST("accounts")
    suspend fun createAccount(
        @Body request: AccountMutationRequestDto
    ): AccountDto

    @GET("accounts/{id}")
    suspend fun getAccount(@Path("id") id: Int): AccountDto

    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: Int,
        @Body request: AccountMutationRequestDto
    ): AccountDto

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsForPeriod(
        @Path("accountId") accountId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionResponseDto>

    @POST("transactions")
    suspend fun createTransaction(
        @Body request: TransactionMutationRequestDto
    ): TransactionMutationResponseDto

    @GET("transactions/{id}")
    suspend fun getTransaction(
        @Path("id") id: Int
    ): TransactionResponseDto

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Int,
        @Body request: TransactionMutationRequestDto
    ): TransactionMutationResponseDto
}
