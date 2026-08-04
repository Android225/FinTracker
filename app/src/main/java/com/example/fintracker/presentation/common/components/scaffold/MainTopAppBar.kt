package com.example.fintracker.presentation.common.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    selectedDate: LocalDate,
    onMenuClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
){
    val locale = LocalConfiguration.current.locales[0]
    val dateLabel = remember(selectedDate, locale) {
        selectedDate.format(
            DateTimeFormatter.ofPattern("d MMMM", locale)
        )
    }
    TopAppBar(
        title = {},
        navigationIcon = {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable{
                        onMenuClick()
                    }
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    modifier = Modifier.size(height = 16.dp, width = 14.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.calendar_month),
                    contentDescription = stringResource(R.string.pick_date_button)
                    , tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = onAnalyticsClick,
                ) {
                Icon(
                    painter = painterResource(id = R.drawable.analytics),
                    contentDescription = stringResource(R.string.analytics_button),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.padding(start = 4.dp))
            IconButton(modifier = Modifier.padding(end = 12.dp).size(48.dp),
                onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.sliders_horizontal),
                    contentDescription = stringResource(R.string.settings_open_description),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
