package com.example.fintracker.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import java.util.UUID
import kotlinx.coroutines.flow.first

class SyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            SyncWorkerEntryPoint::class.java
        )
        return when (val result = entryPoint.syncCoordinator().sync()) {
            is DataResult.Success<*> -> Result.success()
            is DataResult.Failure -> when (result.error) {
                AppError.Network, AppError.RateLimited -> Result.retry()
                else -> Result.failure(
                    workDataOf(ERROR_KEY to result.error.toWorkErrorCode())
                )
            }
        }
    }

    companion object {
        const val ERROR_KEY = "sync_error"
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncWorkerEntryPoint {
    fun syncCoordinator(): SyncCoordinator
}

object SyncScheduler {
    private val networkConstraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedule(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val periodic = PeriodicWorkRequestBuilder<SyncWorker>(2, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .build()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodic
        )
    }

    fun enqueueNow(context: Context): UUID {
        val immediate = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraint)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            immediate
        )
        return immediate.id
    }

    suspend fun awaitCompletion(context: Context, workId: UUID): WorkInfo =
        requireNotNull(
            WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(workId)
            .first { workInfo -> workInfo?.state?.isFinished == true }
        )

    fun failureError(workInfo: WorkInfo): AppError? {
        if (workInfo.state != WorkInfo.State.FAILED) return null
        return when (workInfo.outputData.getString(SyncWorker.ERROR_KEY)) {
            "bad_request" -> AppError.BadRequest
            "unauthorized" -> AppError.Unauthorized
            "not_found" -> AppError.NotFound
            "conflict" -> AppError.Conflict
            "rate_limited" -> AppError.RateLimited
            "server" -> AppError.Server
            "invalid_data" -> AppError.InvalidData()
            else -> AppError.Unknown()
        }
    }

    private const val PERIODIC_WORK_NAME = "periodic-finance-sync"
    private const val IMMEDIATE_WORK_NAME = "immediate-finance-sync"
}

private fun AppError.toWorkErrorCode(): String = when (this) {
    AppError.Network -> "network"
    AppError.BadRequest -> "bad_request"
    AppError.Unauthorized -> "unauthorized"
    AppError.NotFound -> "not_found"
    AppError.Conflict -> "conflict"
    AppError.RateLimited -> "rate_limited"
    AppError.Server -> "server"
    is AppError.InvalidData -> "invalid_data"
    is AppError.Unknown -> "unknown"
}
