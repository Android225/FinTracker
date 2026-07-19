package com.example.fintracker.data.repository

import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.time.LocalDate

class RemoteRepositoriesTest {
    @Test
    fun `accounts repository maps successful response`() = withServer { server, api ->
        server.enqueue(jsonResponse(ACCOUNT_JSON))

        val result = runBlocking {
            AccountsRepositoryImpl(api, Dispatchers.Unconfined).getAccounts()
        }

        val accounts = (result as DataResult.Success).data
        assertEquals(BigDecimal("1000.00"), accounts.single().balance)
        assertEquals("💵", accounts.single().emoji)
        assertEquals("/accounts", server.takeRequest().path)
    }

    @Test
    fun `transactions repository sends explicit period and maps response`() =
        withServer { server, api ->
            server.enqueue(jsonResponse(TRANSACTION_JSON))

            val result = runBlocking {
                TransactionsRepositoryImpl(api, Dispatchers.Unconfined)
                    .getTransactionsForPeriod(
                        accountId = 7,
                        startDate = LocalDate.of(2026, 7, 1),
                        endDate = LocalDate.of(2026, 7, 19)
                    )
            }

            val transactions = (result as DataResult.Success).data
            assertEquals(BigDecimal("500.00"), transactions.single().amount)
            assertEquals(
                "/transactions/account/7/period?startDate=2026-07-01&endDate=2026-07-19",
                server.takeRequest().path
            )
        }

    @Test
    fun `repository maps authorization and server failures`() = withServer { server, api ->
        val repository = AccountsRepositoryImpl(api, Dispatchers.Unconfined)
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(500))

        val unauthorized = runBlocking { repository.getAccounts() }
        val serverError = runBlocking { repository.getAccounts() }

        assertEquals(AppError.Unauthorized, (unauthorized as DataResult.Failure).error)
        assertEquals(AppError.Server, (serverError as DataResult.Failure).error)
    }

    @Test
    fun `repository maps invalid response fields to invalid data`() = withServer { server, api ->
        server.enqueue(jsonResponse(TRANSACTION_JSON.replace("500.00", "invalid")))

        val result = runBlocking {
            TransactionsRepositoryImpl(api, Dispatchers.Unconfined)
                .getTransactionsForPeriod(
                    accountId = 7,
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2026, 7, 19)
                )
        }

        assertTrue((result as DataResult.Failure).error is AppError.InvalidData)
    }

    private fun withServer(block: (MockWebServer, FinanceApi) -> Unit) {
        val server = MockWebServer()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(FinanceApi::class.java)
        try {
            block(server, api)
        } finally {
            server.shutdown()
        }
    }

    private fun jsonResponse(body: String): MockResponse = MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(body)

    private companion object {
        const val ACCOUNT_JSON = """[
            {
              "id": 1,
              "userId": 2,
              "name": "Основной счёт",
              "emoji": "💵",
              "balance": "1000.00",
              "currency": "RUB",
              "createdAt": "2026-07-19T10:00:00Z",
              "updatedAt": "2026-07-19T10:00:00Z"
            }
        ]"""

        const val TRANSACTION_JSON = """[
            {
              "id": 1,
              "account": {
                "id": 7,
                "name": "Основной счёт",
                "emoji": "💵",
                "balance": "1000.00",
                "currency": "RUB"
              },
              "category": {
                "id": 1,
                "name": "Зарплата",
                "emoji": "💰",
                "isIncome": true
              },
              "amount": "500.00",
              "transactionDate": "2026-07-19T10:00:00Z",
              "comment": null,
              "createdAt": "2026-07-19T10:00:00Z",
              "updatedAt": "2026-07-19T10:00:00Z"
            }
        ]"""
    }
}
