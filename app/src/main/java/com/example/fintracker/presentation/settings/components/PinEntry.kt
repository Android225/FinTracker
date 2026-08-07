package com.example.fintracker.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fintracker.R

const val PinInputTestTag = "pin_input"

@Composable
fun PinEntry(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    requestFocus: Boolean = true,
    focusRequestKey: Int = 0
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val description = stringResource(R.string.pin_input_description)

    LaunchedEffect(requestFocus, enabled, focusRequestKey) {
        if (requestFocus && enabled) {
            withFrameNanos { }
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(input.filter(Char::isDigit).take(PinLength))
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .testTag(PinInputTestTag)
            .semantics { contentDescription = description },
        enabled = enabled,
        textStyle = TextStyle(color = Color.Transparent),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(PinLength) { index ->
                    val color = when {
                        isError -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Surface(
                        modifier = Modifier.size(16.dp),
                        shape = CircleShape,
                        color = if (index < value.length) color else Color.Transparent,
                        border = BorderStroke(2.dp, color)
                    ) {}
                }
            }
        }
    )
}

private const val PinLength = 4
