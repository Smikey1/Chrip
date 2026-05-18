package com.twugteam.admin.chat.data.network

import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

class ConnectionRetryHandler(
    private val connectionErrorHandler: ConnectionErrorHandler
) {

    private var shouldSkipBackoffDelay = false
    fun shouldRetry(cause: Throwable, attempt: Long): Boolean {
        return connectionErrorHandler.isRetriableError(cause)
    }

    suspend fun applyRetryDelay(attempt: Long) {
        if (!shouldSkipBackoffDelay) {
            val delayTime = createBackoffDelay(attempt)
            delay(delayTime)
        } else {
            shouldSkipBackoffDelay = false
        }
    }

    fun resetDelay() {
        shouldSkipBackoffDelay = true
    }

    private fun createBackoffDelay(attempt: Long): Long {
        val delayTime = (2f.pow(attempt.toInt()) * 2000L).toLong()
        val maxDelay = 30_000L
        return min(delayTime, maxDelay)

    }
}