package com.example.fintracker.presentation.screens.editor

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.presentation.common.components.bottomsheet.FinTrackerBottomSheet
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.list.ListItemContentText
import com.example.fintracker.presentation.common.components.list.ListItemLeadEmoji
import com.example.fintracker.presentation.common.components.list.ListItemLeadIcon
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private enum class TransactionPicker {
    Category,
    Account
}

private data class EditorChoiceOption(
    val key: String,
    val emoji: String,
    val title: String,
    val supportingText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorHost(
    viewModel: TransactionEditorViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var picker by remember { mutableStateOf<TransactionPicker?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    ErrorSnackbarEffect(
        errors = viewModel.errors,
        snackbarHostState = snackbarHostState,
        onRetry = viewModel::save
    )
    LaunchedEffect(viewModel) {
        viewModel.saved.collect {
            onSaved()
        }
    }
    if (!state.visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    FinTrackerBottomSheet(
        onDismissRequest = viewModel::dismiss,
        sheetState = sheetState,
        dragHandleHeight = 16.dp
    ) {
        Box {
            TransactionEditorContent(
                state = state,
                onAmountChange = viewModel::setAmount,
                onCommentChange = viewModel::setComment,
                onCategoryClick = { picker = TransactionPicker.Category },
                onDateClick = { showDatePicker = true },
                onTimeClick = { showTimePicker = true },
                onAccountClick = { picker = TransactionPicker.Account },
                onSave = viewModel::save
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    when (picker) {
        TransactionPicker.Category -> EditorChoiceBottomSheet(
            title = stringResource(R.string.transaction_category),
            options = state.categories.map {
                EditorChoiceOption(
                    key = it.id.toString(),
                    emoji = it.emoji,
                    title = it.name
                )
            },
            selectedKey = state.categoryId?.toString(),
            onSelect = {
                viewModel.setCategory(it.toInt())
                picker = null
            },
            onDismiss = { picker = null }
        )
        TransactionPicker.Account -> EditorChoiceBottomSheet(
            title = stringResource(R.string.account),
            options = state.accounts.map {
                EditorChoiceOption(
                    key = it.id.toString(),
                    emoji = it.emoji,
                    title = it.name,
                    supportingText = it.currency
                )
            },
            selectedKey = state.accountId?.toString(),
            onSelect = {
                viewModel.setAccount(it.toLong())
                picker = null
            },
            onDismiss = { picker = null }
        )
        null -> Unit
    }

    if (showDatePicker) {
        EditorDatePicker(
            date = state.date,
            onSelect = {
                viewModel.setDate(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    if (showTimePicker) {
        EditorTimePicker(
            time = state.time,
            onSelect = {
                viewModel.setTime(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun TransactionEditorContent(
    state: TransactionEditorState,
    onAmountChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onAccountClick: () -> Unit,
    onSave: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val account = state.accounts.firstOrNull { it.id == state.accountId }
    val category = state.categories.firstOrNull { it.id == state.categoryId }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(top = 24.dp, bottom = 20.dp)
    ) {
        AmountField(
            value = state.amount,
            currency = state.selectedAccountCurrency,
            onValueChange = onAmountChange,
            error = state.validationError == TransactionValidationError.Amount
        )
        Spacer(Modifier.height(16.dp))
        EditorSelectionRow(
            icon = R.drawable.analytics_filter_category,
            label = stringResource(R.string.transaction_category),
            value = category?.name.orEmpty(),
            onClick = onCategoryClick
        )
        EditorSelectionRow(
            icon = R.drawable.calendar_month,
            label = stringResource(R.string.date),
            value = state.date.format(
                DateTimeFormatter.ofPattern("d MMMM", locale)
            ),
            onClick = onDateClick
        )
        EditorSelectionRow(
            icon = R.drawable.calendar_month,
            label = stringResource(R.string.time),
            value = state.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            onClick = onTimeClick
        )
        EditorSelectionRow(
            icon = R.drawable.accounts,
            label = stringResource(R.string.account),
            value = account?.name.orEmpty(),
            onClick = onAccountClick
        )
        OutlinedTextField(
            value = state.comment,
            onValueChange = onCommentChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            label = { Text(stringResource(R.string.comment)) },
            singleLine = true
        )
        EditorConfirmButton(enabled = !state.loading && !state.saving, onClick = onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorHost(
    viewModel: AccountEditorViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCurrencies by remember { mutableStateOf(false) }

    ErrorSnackbarEffect(
        errors = viewModel.errors,
        snackbarHostState = snackbarHostState,
        onRetry = viewModel::save
    )
    LaunchedEffect(viewModel) {
        viewModel.saved.collect { onSaved() }
    }
    if (!state.visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    FinTrackerBottomSheet(
        onDismissRequest = viewModel::dismiss,
        sheetState = sheetState,
        dragHandleHeight = 16.dp
    ) {
        Box {
            AccountEditorContent(
                state = state,
                onNameChange = viewModel::setName,
                onEmojiChange = viewModel::setEmoji,
                onBalanceChange = viewModel::setBalance,
                onCurrencyClick = { showCurrencies = true },
                onSave = viewModel::save
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    if (showCurrencies) {
        EditorChoiceBottomSheet(
            title = stringResource(R.string.currency),
            options = AppCurrency.entries.map { currency ->
                EditorChoiceOption(
                    key = currency.code,
                    emoji = currencyFlag(currency),
                    title = stringResource(currencyName(currency)),
                    supportingText = currency.code
                )
            },
            selectedKey = state.currency,
            onSelect = {
                viewModel.setCurrency(it)
                showCurrencies = false
            },
            onDismiss = { showCurrencies = false }
        )
    }
}

@Composable
private fun AccountEditorContent(
    state: AccountEditorState,
    onNameChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onBalanceChange: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(top = 20.dp, bottom = 20.dp)
    ) {
        Text(
            text = stringResource(
                if (state.accountId == null) R.string.add_account else R.string.edit_account
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.emoji,
                onValueChange = onEmojiChange,
                modifier = Modifier.width(88.dp),
                label = { Text(stringResource(R.string.account_emoji)) },
                singleLine = true
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.account_name)) },
                isError = state.validationError == AccountValidationError.Name,
                singleLine = true
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.balance_all),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AmountField(
            value = state.balance,
            currency = state.currency,
            onValueChange = onBalanceChange,
            error = state.validationError == AccountValidationError.Balance
        )
        Spacer(Modifier.height(16.dp))
        EditorSelectionRow(
            icon = R.drawable.accounts,
            label = stringResource(R.string.currency),
            value = state.currency,
            onClick = onCurrencyClick
        )
        EditorConfirmButton(enabled = !state.loading && !state.saving, onClick = onSave)
    }
}

@Composable
private fun AmountField(
    value: String,
    currency: String,
    onValueChange: (String) -> Unit,
    error: Boolean
) {
    val currencySuffix = " ${currencySymbol(currency)}"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(58.dp),
            textStyle = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = AmountVisualTransformation(currencySuffix),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "0$currencySuffix",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            }
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
        )
        if (error) {
            Text(
                text = stringResource(R.string.invalid_amount),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private class AmountVisualTransformation(
    private val suffix: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val inputLength = text.length
        return TransformedText(
            text = AnnotatedString(text.text + suffix),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int =
                    offset.coerceIn(0, inputLength)

                override fun transformedToOriginal(offset: Int): Int =
                    offset.coerceIn(0, inputLength)
            }
        )
    }
}

@Composable
private fun EditorSelectionRow(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    ListItem(
        lead = {
            ListItemLeadIcon(
                icon = icon,
                iconSize = 18.dp,
                circleBorderColor = MaterialTheme.colorScheme.outline
            )
        },
        content = { ListItemContentText(label) },
        trail = {
            Surface(
                onClick = onClick,
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = value.ifBlank { stringResource(R.string.field_required) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
}

@Composable
private fun EditorConfirmButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, end = 32.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface
        ) {
            if (enabled) {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.analytics_check),
                    contentDescription = stringResource(R.string.save),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorChoiceBottomSheet(
    title: String,
    options: List<EditorChoiceOption>,
    selectedKey: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val maxListHeight = (LocalConfiguration.current.screenHeightDp * 0.6f).dp

    FinTrackerBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandleHeight = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier.heightIn(max = maxListHeight)
            ) {
                itemsIndexed(
                    items = options,
                    key = { _, option -> option.key }
                ) { index, option ->
                    val selected = option.key == selectedKey
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected,
                                onClick = { onSelect(option.key) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ListItemLeadEmoji(option.emoji)
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            option.supportingText?.let { supportingText ->
                                Text(
                                    text = supportingText,
                                    modifier = Modifier.padding(top = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (selected) {
                            Icon(
                                painter = painterResource(R.drawable.analytics_check),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (index != options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorDatePicker(
    date: LocalDate,
    onSelect: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        onSelect(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                }
            ) { Text(stringResource(R.string.ready)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTimePicker(
    time: LocalTime,
    onSelect: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.time)) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onSelect(LocalTime.of(state.hour, state.minute)) }) {
                Text(stringResource(R.string.ready))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

private fun currencySymbol(currency: String): String = when (currency) {
    "RUB" -> "₽"
    "USD" -> "$"
    "EUR" -> "€"
    else -> currency
}

private fun currencyFlag(currency: AppCurrency): String = when (currency) {
    AppCurrency.Rub -> "🇷🇺"
    AppCurrency.Usd -> "🇺🇸"
    AppCurrency.Eur -> "🇪🇺"
    AppCurrency.Gbp -> "🇬🇧"
    AppCurrency.Cny -> "🇨🇳"
}

@StringRes
private fun currencyName(currency: AppCurrency): Int = when (currency) {
    AppCurrency.Rub -> R.string.currency_rub
    AppCurrency.Usd -> R.string.currency_usd
    AppCurrency.Eur -> R.string.currency_eur
    AppCurrency.Gbp -> R.string.currency_gbp
    AppCurrency.Cny -> R.string.currency_cny
}
