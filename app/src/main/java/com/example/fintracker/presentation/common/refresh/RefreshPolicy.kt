package com.example.fintracker.presentation.common.refresh

import java.time.Clock

// Ограничивает автоматические обновления, но не блокирует явный запрос пользователя.
internal class RefreshPolicy(
    private val clock: Clock,
    private val staleAfterMillis: Long = DEFAULT_STALE_AFTER_MILLIS
) {
    private var lastSuccessfulRefreshAt: Long? = null

    init {
        require(staleAfterMillis >= 0) { "Refresh interval cannot be negative" }
    }

    // Возвращает true для принудительного запроса или когда сохранённые данные устарели.
    fun shouldRefresh(force: Boolean): Boolean {
        if (force) return true
        val lastRefresh = lastSuccessfulRefreshAt ?: return true
        return clock.millis() - lastRefresh >= staleAfterMillis
    }

    // Учитывает только успешную загрузку, чтобы ошибка не откладывала следующую попытку.
    fun markSuccessfulRefresh() {
        lastSuccessfulRefreshAt = clock.millis()
    }

    private companion object {
        const val DEFAULT_STALE_AFTER_MILLIS = 30_000L
    }
}
