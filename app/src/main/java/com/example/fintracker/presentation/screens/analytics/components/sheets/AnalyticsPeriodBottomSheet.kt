package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.presentation.screens.analytics.AnalyticsPeriodOption

@Composable
fun AnalyticsPeriodBottomSheet(
    options: List<AnalyticsPeriodOption>,
    onSelect: (AnalyticsPeriodOption) -> Unit,
    onCustomPeriodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(548.dp)
    ) {
        AnalyticsSheetHeader(title = stringResource(R.string.period_for_calculate_transactions))
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (option.subtitle == null) 52.dp else 64.dp)
                    .clickable {
                        if (option.kind == AnalyticsPeriodKind.Custom) {
                            onCustomPeriodClick()
                        } else {
                            onSelect(option)
                        }
                    }
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.title,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    option.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            modifier = Modifier.padding(top = 2.dp),
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AnalyticsCheckmark(visible = option.selected)
            }
            if (index != options.lastIndex) AnalyticsSheetDivider()
        }
    }
}
