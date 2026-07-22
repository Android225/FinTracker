package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.presentation.screens.analytics.displayName

@Composable
fun AnalyticsTypeBottomSheet(
    options: List<TransactionType>,
    selectedType: TransactionType,
    onApply: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftType by remember(selectedType) { mutableStateOf(selectedType) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        AnalyticsSheetHeader(title = stringResource(R.string.type_transaction))
        options.forEachIndexed { index, type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { draftType = type }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type.displayName,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AnalyticsSelectionCircle(
                    selected = type == draftType,
                    onClick = { draftType = type }
                )
            }
            if (index != options.lastIndex) AnalyticsSheetDivider()
        }

        AnalyticsSheetButton(
            text = stringResource(R.string.ready),
            onClick = { onApply(draftType) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}
