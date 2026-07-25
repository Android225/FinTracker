package com.example.fintracker.data.remote.util

import java.util.concurrent.TimeUnit

internal object ServerRetryPolicy {
    const val MAX_RETRIES = 3
    val DELAY_MILLIS: Long = TimeUnit.SECONDS.toMillis(2)
}
