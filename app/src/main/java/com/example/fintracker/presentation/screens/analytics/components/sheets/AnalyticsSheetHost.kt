package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fintracker.presentation.common.FinTrackerBottomSheet
import com.example.fintracker.presentation.screens.analytics.AnalyticsContent
import com.example.fintracker.presentation.screens.analytics.AnalyticsEvent
import com.example.fintracker.presentation.screens.analytics.AnalyticsPeriodKind
import com.example.fintracker.presentation.screens.analytics.domain.AnalyticsSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheetHost(
    sheet: AnalyticsSheet,
    content: AnalyticsContent,
    onEvent: (AnalyticsEvent) -> Unit,
    onSheetChange: (AnalyticsSheet) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    FinTrackerBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandleHeight = when (sheet) {
            AnalyticsSheet.Detail -> 24.dp
            AnalyticsSheet.CustomPeriod -> 36.dp
            else -> 32.dp
        }
    ) {
        when (sheet) {
            AnalyticsSheet.Detail -> AnalyticsDetailBottomSheet(
                totalAmount = content.totalAmount,
                breakdown = content.breakdown
            )
            AnalyticsSheet.Type -> AnalyticsTypeBottomSheet(
                options = content.typeOptions,
                selectedType = content.selectedType,
                onApply = { type ->
                    onEvent(AnalyticsEvent.SelectType(type))
                    onDismiss()
                }
            )
            AnalyticsSheet.Period -> AnalyticsPeriodBottomSheet(
                options = content.periodOptions,
                onSelect = { option ->
                    onEvent(AnalyticsEvent.SelectPeriod(option.kind, option.period))
                    onDismiss()
                },
                onCustomPeriodClick = { onSheetChange(AnalyticsSheet.CustomPeriod) }
            )
            AnalyticsSheet.Categories -> AnalyticsCategoriesBottomSheet(
                options = content.categoryOptions,
                onApply = { categoryIds ->
                    onEvent(AnalyticsEvent.SelectCategories(categoryIds))
                    onDismiss()
                }
            )
            AnalyticsSheet.Account -> AnalyticsAccountBottomSheet(
                options = content.accountOptions,
                onSelect = { account ->
                    onEvent(AnalyticsEvent.SelectAccount(account.id))
                    onDismiss()
                }
            )
            AnalyticsSheet.CustomPeriod -> AnalyticsDateRangeBottomSheet(
                initialPeriod = content.selectedPeriod,
                onCancel = { onSheetChange(AnalyticsSheet.Period) },
                onApply = { period ->
                    onEvent(
                        AnalyticsEvent.SelectPeriod(
                            kind = AnalyticsPeriodKind.Custom,
                            period = period
                        )
                    )
                    onDismiss()
                }
            )
        }
    }
}
