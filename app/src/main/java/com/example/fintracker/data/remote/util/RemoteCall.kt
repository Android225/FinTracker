package com.example.fintracker.data.remote.util

import com.example.fintracker.data.remote.mapper.RemoteDataException
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

internal suspend fun <T> executeRemoteCall(block: suspend () -> T): DataResult<T> =
    try {
        DataResult.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: IOException) {
        DataResult.Failure(AppError.Network)
    } catch (exception: HttpException) {
        DataResult.Failure(exception.toAppError())
    } catch (exception: RemoteDataException) {
        DataResult.Failure(AppError.InvalidData(exception.message))
    } catch (exception: JsonParseException) {
        DataResult.Failure(AppError.InvalidData(exception.message))
    } catch (exception: Exception) {
        DataResult.Failure(AppError.Unknown(exception.message))
    }

private fun HttpException.toAppError(): AppError = when (code()) {
    400 -> AppError.BadRequest
    401, 403 -> AppError.Unauthorized
    404 -> AppError.NotFound
    429 -> AppError.RateLimited
    in 400..499 -> AppError.BadRequest
    in 500..599 -> AppError.Server
    else -> AppError.Unknown(message())
}
