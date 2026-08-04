package com.example.fintracker.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.fintracker.presentation.settings.components.PinEntry

@Composable
fun UnlockScreen(
    verifyingPin: Boolean,
    pinRejected: Boolean,
    biometricAvailable: Boolean,
    onPinChange: () -> Unit,
    onPinComplete: (String) -> Unit,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        if (pin.length == PinLength) onPinComplete(pin)
    }
    LaunchedEffect(pinRejected) {
        if (pinRejected) pin = ""
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(88.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.settings_pin),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.size(28.dp))
                Text(
                    text = stringResource(R.string.unlock_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.unlock_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.size(36.dp))
                Box(modifier = Modifier.width(112.dp)) {
                    PinEntry(
                        value = pin,
                        onValueChange = {
                            onPinChange()
                            pin = it
                        },
                        enabled = !verifyingPin,
                        isError = pinRejected
                    )
                }
                Spacer(Modifier.size(20.dp))
                when {
                    verifyingPin -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Text(
                            text = stringResource(R.string.unlock_verifying),
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    pinRejected -> Text(
                        text = stringResource(R.string.unlock_wrong_pin),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                if (biometricAvailable) {
                    Spacer(Modifier.size(28.dp))
                    OutlinedButton(onClick = onBiometricClick) {
                        Icon(
                            painter = painterResource(R.drawable.settings_biometric),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(R.string.unlock_biometric))
                    }
                }
            }
        }
    }
}

private const val PinLength = 4
