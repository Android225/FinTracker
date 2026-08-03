package com.example.fintracker.data.remote.util

import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import retrofit2.HttpException
import retrofit2.Response

class RemoteCallTest {
    @Test
    fun `maps io exception to network error`() = runBlocking {
        val result = executeRemoteCall<Unit> { throw IOException("offline") }

        assertEquals(AppError.Network, (result as DataResult.Failure).error)
    }

    @Test
    fun `does not swallow coroutine cancellation`() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                executeRemoteCall<Unit> { throw CancellationException("cancelled") }
            }
        }
    }

    @Test
    fun `maps conflict response to conflict error`() = runBlocking {
        val result = executeRemoteCall<Unit> {
            throw HttpException(Response.error<Unit>(409, "".toResponseBody()))
        }

        assertEquals(AppError.Conflict, (result as DataResult.Failure).error)
    }
}
