package com.example.fintracker.presentation.common.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fintracker.domain.error.AppError

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
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
        modifier = modifier.fillMaxSize(),
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

// Сообщение об ошибке с кнопкой повтора.
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
            text = error.messageForUser(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetryClick
        ) {
            Text(text = "Повторить")
        }
    }
}

private fun AppError.messageForUser(): String {
    return when (this) {
        AppError.Network -> "Не удалось подключиться к интернету"
        AppError.BadRequest -> "Сервер не смог обработать запрос"
        AppError.Unauthorized -> "Нужна авторизация"
        AppError.NotFound -> "Данные не найдены"
        AppError.RateLimited -> "Слишком много запросов. Попробуйте позже"
        AppError.Server -> "На сервере что-то пошло не так"
        is AppError.InvalidData -> "Сервер вернул некорректные данные"
        is AppError.Unknown -> message ?: "Неизвестная ошибка"
    }
}
