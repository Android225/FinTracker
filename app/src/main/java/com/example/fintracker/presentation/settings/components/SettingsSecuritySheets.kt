package com.example.fintracker.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fintracker.R

@Composable
fun PinSettingsSheet(
    pinConfigured: Boolean,
    saving: Boolean,
    onPinComplete: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        if (pin.length == PinLength) {
            onPinComplete(pin)
            pin = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsSheetTitle(
            stringResource(
                if (pinConfigured) R.string.pin_change_title else R.string.pin_create_title
            )
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.pin_new_prompt),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        Box(modifier = Modifier.padding(horizontal = 118.dp)) {
            PinEntry(
                value = pin,
                onValueChange = { pin = it },
                enabled = !saving
            )
        }
    }
}

@Composable
fun BiometricSettingsSheet(
    enabled: Boolean,
    pinConfigured: Boolean,
    biometricAvailable: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val supportingText = when {
        !pinConfigured -> stringResource(R.string.biometric_requires_pin)
        !biometricAvailable -> stringResource(R.string.biometric_unavailable)
        else -> stringResource(R.string.biometric_privacy)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.biometric_face_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.settings_biometric),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.biometric_description),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.biometric_switch),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    enabled = pinConfigured && biometricAvailable
                )
            }
        }
        Text(
            text = supportingText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 28.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private const val PinLength = 4
