package com.example.fintracker.presentation.auth

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricLauncher internal constructor(
    private val prompt: BiometricPrompt
) {
    fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String
    ) {
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setNegativeButtonText(cancelText)
            .build()
        prompt.authenticate(info)
    }
}

@Composable
fun rememberBiometricLauncher(
    onSuccess: () -> Unit,
    onCancelled: () -> Unit = {},
    onError: (String) -> Unit = {}
): BiometricLauncher {
    val context = LocalContext.current
    val activity = requireNotNull(context.findFragmentActivity())
    val currentSuccess = rememberUpdatedState(onSuccess)
    val currentCancelled = rememberUpdatedState(onCancelled)
    val currentError = rememberUpdatedState(onError)
    return remember(activity) {
        val executor = ContextCompat.getMainExecutor(activity)
        BiometricLauncher(
            BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        currentSuccess.value()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_USER_CANCELED
                        ) {
                            currentCancelled.value()
                        } else {
                            currentError.value(errString.toString())
                        }
                    }
                }
            )
        )
    }
}

fun isBiometricAvailable(context: Context): Boolean =
    BiometricManager.from(context).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    ) == BiometricManager.BIOMETRIC_SUCCESS

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
