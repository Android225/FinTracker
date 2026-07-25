package com.example.fintracker.presentation.screens.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.sync.SyncScheduler
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class TransactionEditorState(
    val visible: Boolean = false,
    val transactionId: Long? = null,
    val type: TransactionType = TransactionType.Expense,
    val amount: String = "",
    val comment: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now().withSecond(0).withNano(0),
    val accountId: Long? = null,
    val categoryId: Int? = null,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val loading: Boolean = false,
    val saving: Boolean = false,
    val validationError: TransactionValidationError? = null
) {
    val selectedAccountCurrency: String
        get() = accounts.firstOrNull { it.id == accountId }?.currency ?: DEFAULT_CURRENCY

    private companion object {
        const val DEFAULT_CURRENCY = "RUB"
    }
}

enum class TransactionValidationError {
    Amount,
    Account,
    Category
}

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    private val clock: Clock,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionEditorState())
    val state: StateFlow<TransactionEditorState> = _state.asStateFlow()

    private val savedChannel = Channel<Unit>(Channel.BUFFERED)
    val saved: Flow<Unit> = savedChannel.receiveAsFlow()

    private val errorChannel = Channel<AppError>(Channel.BUFFERED)
    val errors: Flow<AppError> = errorChannel.receiveAsFlow()

    private var loadJob: Job? = null

    fun open(type: TransactionType, transactionId: Long?) {
        loadJob?.cancel()
        _state.value = TransactionEditorState(
            visible = true,
            transactionId = transactionId,
            type = type,
            date = LocalDate.now(clock),
            time = LocalTime.now(clock).withSecond(0).withNano(0),
            loading = true
        )
        loadJob = viewModelScope.launch {
            val accounts = accountsRepository.getAccounts()
            val categories = categoriesRepository.getCategories(type == TransactionType.Income)
            if (accounts is DataResult.Failure) {
                errorChannel.send(accounts.error)
                dismiss()
                return@launch
            }
            if (categories is DataResult.Failure) {
                errorChannel.send(categories.error)
                dismiss()
                return@launch
            }
            val accountItems = (accounts as DataResult.Success).data
            val categoryItems = (categories as DataResult.Success).data
            val transaction = transactionId?.let {
                when (val result = transactionsRepository.getTransaction(it)) {
                    is DataResult.Success -> result.data
                    is DataResult.Failure -> {
                        errorChannel.send(result.error)
                        null
                    }
                }
            }
            val zone = clock.zone
            _state.value = _state.value.copy(
                amount = transaction?.amount?.toPlainString().orEmpty(),
                comment = transaction?.comment.orEmpty(),
                date = transaction?.transactionDate?.atZone(zone)?.toLocalDate()
                    ?: LocalDate.now(clock),
                time = transaction?.transactionDate?.atZone(zone)?.toLocalTime()
                    ?.withSecond(0)?.withNano(0)
                    ?: LocalTime.now(clock).withSecond(0).withNano(0),
                accountId = transaction?.account?.id ?: accountItems.firstOrNull()?.id,
                categoryId = transaction?.category?.id ?: categoryItems.firstOrNull()?.id,
                accounts = accountItems,
                categories = categoryItems,
                loading = false
            )
        }
    }

    fun dismiss() {
        _state.value = TransactionEditorState()
    }

    fun setAmount(value: String) {
        if (value.count { it == '.' || it == ',' } <= 1 &&
            value.all { it.isDigit() || it == '.' || it == ',' }
        ) {
            _state.value = _state.value.copy(
                amount = value,
                validationError = null
            )
        }
    }

    fun setComment(value: String) {
        _state.value = _state.value.copy(comment = value)
    }

    fun setDate(value: LocalDate) {
        _state.value = _state.value.copy(date = value)
    }

    fun setTime(value: LocalTime) {
        _state.value = _state.value.copy(time = value)
    }

    fun setAccount(value: Long) {
        _state.value = _state.value.copy(accountId = value, validationError = null)
    }

    fun setCategory(value: Int) {
        _state.value = _state.value.copy(categoryId = value, validationError = null)
    }

    fun save() {
        val state = _state.value
        if (state.saving) return
        val amount = state.amount.replace(',', '.').toBigDecimalOrNull()
        val validationError = when {
            amount == null || amount <= BigDecimal.ZERO -> TransactionValidationError.Amount
            state.accountId == null -> TransactionValidationError.Account
            state.categoryId == null -> TransactionValidationError.Category
            else -> null
        }
        if (validationError != null) {
            _state.value = state.copy(validationError = validationError)
            return
        }

        _state.value = state.copy(saving = true, validationError = null)
        viewModelScope.launch {
            val transactionDate = state.date
                .atTime(state.time)
                .atZone(clock.zone)
                .toInstant()
            val result = transactionsRepository.saveTransaction(
                id = state.transactionId,
                draft = TransactionDraft(
                    accountId = requireNotNull(state.accountId),
                    categoryId = requireNotNull(state.categoryId),
                    amount = requireNotNull(amount),
                    transactionDate = transactionDate,
                    comment = state.comment.trim().ifBlank { null }
                )
            )
            when (result) {
                is DataResult.Success -> {
                    val syncWorkId = SyncScheduler.enqueueNow(context)
                    dismiss()
                    savedChannel.send(Unit)
                    SyncScheduler.awaitCompletion(context, syncWorkId)
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
