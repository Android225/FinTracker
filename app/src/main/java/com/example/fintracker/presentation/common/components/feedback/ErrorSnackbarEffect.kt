package com.example.fintracker.presentation.common.components.feedback

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintracker.R
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
    val retry = stringResource(R.string.retry)
    val context = LocalContext.current
    LaunchedEffect(errors, lifecycleOwner, snackbarHostState) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            errors.collect { error ->
                val result = snackbarHostState.showSnackbar(
                    message = error.toUserMessage(context),
                    actionLabel = retry,
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
