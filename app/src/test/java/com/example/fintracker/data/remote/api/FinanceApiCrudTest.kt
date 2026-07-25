package com.example.fintracker.data.remote.api

import com.example.fintracker.data.remote.dto.AccountMutationRequestDto
import com.example.fintracker.data.remote.dto.TransactionMutationRequestDto
import com.example.fintracker.data.remote.util.ServerRetryInterceptor
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FinanceApiCrudTest {
    @Test
    fun `create transaction sends swagger request`() = withServer { server, api ->
        server.enqueue(jsonResponse(TRANSACTION_RESPONSE, 201))

        runBlocking {
            api.createTransaction(
                TransactionMutationRequestDto(
                    accountId = 7,
                    categoryId = 3,
                    amount = "500.00",
                    transactionDate = "2026-07-25T10:00:00Z",
                    comment = "Тест"
                )
            )
        }

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/transactions", request.path)
        val body = request.body.readUtf8()
        check("\"accountId\":7" in body)
        check("\"categoryId\":3" in body)
    }

    @Test
    fun `update account sends all editable fields`() = withServer { server, api ->
        server.enqueue(jsonResponse(ACCOUNT_RESPONSE))

        runBlocking {
            api.updateAccount(
                id = 5,
                request = AccountMutationRequestDto(
                    name = "Накопления",
                    emoji = "🏦",
                    balance = "1200.00",
                    currency = "RUB"
                )
            )
        }

        val request = server.takeRequest()
        assertEquals("PUT", request.method)
        assertEquals("/accounts/5", request.path)
        val body = request.body.readUtf8()
        check("\"name\":\"Накопления\"" in body)
        check("\"currency\":\"RUB\"" in body)
    }

    @Test
    fun `server retry performs three retries for 500`() {
        val server = MockWebServer()
        repeat(3) { server.enqueue(MockResponse().setResponseCode(500)) }
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))
        val client = OkHttpClient.Builder()
            .addInterceptor(ServerRetryInterceptor(maxRetries = 3, retryDelayMillis = 0))
            .build()
        try {
            client.newCall(
                okhttp3.Request.Builder().url(server.url("/retry")).build()
            ).execute().use { response ->
                assertEquals(200, response.code)
            }
            assertEquals(4, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `server retry does not repeat non idempotent post`() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(500))
        val client = OkHttpClient.Builder()
            .addInterceptor(ServerRetryInterceptor(maxRetries = 3, retryDelayMillis = 0))
            .build()
        try {
            client.newCall(
                okhttp3.Request.Builder()
                    .url(server.url("/transactions"))
                    .post(ByteArray(0).toRequestBody())
                    .build()
            ).execute().close()

            assertEquals(1, server.requestCount)
        } finally {
            server.shutdown()
        }
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

    private fun jsonResponse(body: String, code: Int = 200): MockResponse =
        MockResponse()
            .setResponseCode(code)
            .setHeader("Content-Type", "application/json")
            .setBody(body)

    private companion object {
        const val TRANSACTION_RESPONSE = """
            {
              "id": 1,
              "accountId": 7,
              "categoryId": 3,
              "amount": "500.00",
              "transactionDate": "2026-07-25T10:00:00Z",
              "comment": "Тест",
              "createdAt": "2026-07-25T10:00:00Z",
              "updatedAt": "2026-07-25T10:00:00Z"
            }
        """
        const val ACCOUNT_RESPONSE = """
            {
              "id": 5,
              "userId": 2,
              "name": "Накопления",
              "emoji": "🏦",
              "balance": "1200.00",
              "currency": "RUB",
              "createdAt": "2026-07-25T10:00:00Z",
              "updatedAt": "2026-07-25T10:00:00Z"
            }
        """
    }
}
