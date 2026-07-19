package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R

// Локальные цвета компонентов аналитических sheet.
internal val AnalyticsPrimary = Color(0xFF6750A4)
internal val AnalyticsCheckboxColor = Color(0xFF7B5EA7)
internal val AnalyticsOutline = Color(0xFFC4C7C5)
internal val AnalyticsRange = Color(0xFFE7E3F2)

// Заголовок bottom sheet аналитики.
@Composable
internal fun AnalyticsSheetHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        fontSize = 28.sp,
        lineHeight = 33.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// Основная кнопка действия внутри bottom sheet.
@Composable
internal fun AnalyticsSheetButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 52.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AnalyticsPrimary),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Разделитель строк внутри bottom sheet.
@Composable
internal fun AnalyticsSheetDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
    )
}

// Круглая иконка категории с эмодзи.
@Composable
internal fun AnalyticsEmojiIcon(
    emoji: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.background, CircleShape)
            .border(1.dp, AnalyticsOutline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 22.sp, lineHeight = 22.sp)
    }
}

// Радиокнопка для одиночного выбора.
@Composable
internal fun AnalyticsSelectionCircle(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .then(
                if (selected) {
                    Modifier.background(AnalyticsPrimary, CircleShape)
                } else {
                    Modifier.border(2.dp, AnalyticsOutline, CircleShape)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.analytics_check),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White
            )
        }
    }
}

// Галочка выбранного элемента.
@Composable
internal fun AnalyticsCheckmark(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Icon(
            painter = painterResource(R.drawable.analytics_check),
            contentDescription = null,
            modifier = modifier.size(20.dp),
            tint = AnalyticsPrimary
        )
    }
}

// Чекбокс для множественного выбора.
@Composable
internal fun AnalyticsCheckbox(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .then(
                if (checked) {
                    Modifier.background(AnalyticsCheckboxColor, RoundedCornerShape(4.dp))
                } else {
                    Modifier.border(1.dp, AnalyticsOutline, RoundedCornerShape(4.dp))
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.analytics_check),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White
            )
        }
    }
}
