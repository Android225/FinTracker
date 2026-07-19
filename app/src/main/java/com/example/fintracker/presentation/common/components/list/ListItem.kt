package com.example.fintracker.presentation.common.components.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListItem(
    lead: @Composable () -> Unit,
    content: @Composable () -> Unit,
    trail: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        lead()

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            content()
        }

        Spacer(modifier = Modifier.width(16.dp))

        trail()
    }
}

@Composable
fun ListItemLeadEmoji(emoji: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ListItemLeadIcon(
    icon: Int,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconSize: Dp = 32.dp,
    circleSize: Dp = 40.dp,
    circleBorderColor: Color = MaterialTheme.colorScheme.outline
) {
    Box(
        modifier = modifier
            .size(circleSize)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = circleBorderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconColor
        )
    }
}

@Composable
fun ListItemContentText(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ListItemTrailAmount(amount: String) {
    Text(
        text = amount,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun ListItem(
    emoji: String,
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        lead = { ListItemLeadEmoji(emoji) },
        content = { ListItemContentText(title) },
        trail = { ListItemTrailAmount(amount) }
    )
}
