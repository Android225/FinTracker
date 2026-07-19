package com.example.fintracker.presentation.common.components.feedback

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintracker.domain.error.AppError
import kotlinx.coroutines.flow.Flow

// Показывает единый Snackbar для фоновых ошибок и выполняет retry по действию пользователя.
@Composable
fun ErrorSnackbarEffect(
    errors: Flow<AppError>,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnRetry by rememberUpdatedState(onRetry)

    LaunchedEffect(errors, lifecycleOwner, snackbarHostState) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            errors.collect { error ->
                val result = snackbarHostState.showSnackbar(
                    message = error.toUserMessage(),
                    actionLabel = "Повторить",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    currentOnRetry()
                }
            }
        }
    }
}
