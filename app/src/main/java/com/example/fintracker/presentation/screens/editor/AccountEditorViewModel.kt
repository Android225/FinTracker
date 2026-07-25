package com.example.fintracker.presentation.screens.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.sync.SyncScheduler
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class AccountEditorState(
    val visible: Boolean = false,
    val accountId: Long? = null,
    val name: String = "",
    val emoji: String = "💳",
    val balance: String = "",
    val currency: String = "RUB",
    val loading: Boolean = false,
    val saving: Boolean = false,
    val validationError: AccountValidationError? = null
)

enum class AccountValidationError {
    Name,
    Balance
}

@HiltViewModel
class AccountEditorViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(AccountEditorState())
    val state: StateFlow<AccountEditorState> = _state.asStateFlow()

    private val savedChannel = Channel<Unit>(Channel.BUFFERED)
    val saved: Flow<Unit> = savedChannel.receiveAsFlow()

    private val errorChannel = Channel<AppError>(Channel.BUFFERED)
    val errors: Flow<AppError> = errorChannel.receiveAsFlow()

    fun open(accountId: Long?) {
        _state.value = AccountEditorState(
            visible = true,
            accountId = accountId,
            loading = accountId != null
        )
        if (accountId == null) return
        viewModelScope.launch {
            when (val result = accountsRepository.getAccount(accountId)) {
                is DataResult.Success -> {
                    val account = result.data
                    _state.value = _state.value.copy(
                        name = account.name,
                        emoji = account.emoji,
                        balance = account.balance.toPlainString(),
                        currency = account.currency,
                        loading = false
                    )
                }
                is DataResult.Failure -> {
                    errorChannel.send(result.error)
                    dismiss()
                }
            }
        }
    }

    fun dismiss() {
        _state.value = AccountEditorState()
    }

    fun setName(value: String) {
        _state.value = _state.value.copy(name = value, validationError = null)
    }

    fun setEmoji(value: String) {
        _state.value = _state.value.copy(emoji = value.take(4))
    }

    fun setBalance(value: String) {
        if (value.count { it == '.' || it == ',' } <= 1 &&
            value.all { it.isDigit() || it == '.' || it == ',' || it == '-' }
        ) {
            _state.value = _state.value.copy(balance = value, validationError = null)
        }
    }

    fun setCurrency(value: String) {
        _state.value = _state.value.copy(currency = value)
    }

    fun save() {
        val state = _state.value
        if (state.saving) return
        val balance = state.balance.replace(',', '.').toBigDecimalOrNull()
        val error = when {
            state.name.isBlank() -> AccountValidationError.Name
            balance == null -> AccountValidationError.Balance
            else -> null
        }
        if (error != null) {
            _state.value = state.copy(validationError = error)
            return
        }
        _state.value = state.copy(saving = true, validationError = null)
        viewModelScope.launch {
            val result = accountsRepository.saveAccount(
                id = state.accountId,
                draft = AccountDraft(
                    name = state.name.trim(),
                    emoji = state.emoji.ifBlank { "💳" },
                    balance = requireNotNull(balance),
                    currency = state.currency
                )
            )
            when (result) {
                is DataResult.Success -> {
                    SyncScheduler.enqueueNow(context)
                    dismiss()
                    savedChannel.send(Unit)
                }
                is DataResult.Failure -> {
                    _state.value = _state.value.copy(saving = false)
                    errorChannel.send(result.error)
                }
            }
        }
    }
}
