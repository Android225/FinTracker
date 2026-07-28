package com.example.fintracker.presentation.common.components.feedback

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import com.example.fintracker.domain.error.AppError

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorContent(
    error: AppError,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error.toUserMessage(LocalContext.current),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetryClick
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

internal fun AppError.toUserMessage(context: Context): String = when (this) {
    AppError.Network -> context.getString(R.string.error_network)
    AppError.BadRequest -> context.getString(R.string.error_bad_request)
    AppError.Unauthorized -> context.getString(R.string.error_unauthorized)
    AppError.NotFound -> context.getString(R.string.error_not_found)
    AppError.Conflict -> context.getString(R.string.error_account_has_transactions)
    AppError.RateLimited -> context.getString(R.string.error_rate_limited)
    AppError.Server -> context.getString(R.string.error_server)
    is AppError.InvalidData -> context.getString(R.string.error_invalid_data)
    is AppError.Unknown -> message ?: context.getString(R.string.error_unknown)
}
