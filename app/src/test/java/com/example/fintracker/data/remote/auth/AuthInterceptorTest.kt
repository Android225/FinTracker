package com.example.fintracker.data.remote.auth

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {
    @Test
    fun `adds bearer token to request`() {
        withServer { server ->
            val client = clientWithToken("test-token")
            server.enqueue(MockResponse().setResponseCode(200))

            client.newCall(Request.Builder().url(server.url("/accounts")).build())
                .execute()
                .use { response -> assertEquals(200, response.code) }

            assertEquals("Bearer test-token", server.takeRequest().getHeader("Authorization"))
        }
    }

    @Test
    fun `does not add empty authorization header`() {
        withServer { server ->
            val client = clientWithToken("")
            server.enqueue(MockResponse().setResponseCode(200))

            client.newCall(Request.Builder().url(server.url("/accounts")).build())
                .execute()
                .close()

            assertNull(server.takeRequest().getHeader("Authorization"))
        }
    }

    private fun clientWithToken(token: String): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(AccessTokenProvider { token }))
        .build()

    private fun withServer(block: (MockWebServer) -> Unit) {
        val server = MockWebServer()
        try {
            block(server)
        } finally {
            server.shutdown()
        }
    }
}
