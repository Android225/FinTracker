package com.example.fintracker.data.settings

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

class PinHasher @Inject constructor() {
    fun create(pin: String): StoredPin {
        val salt = ByteArray(SaltSize).also(SecureRandom()::nextBytes)
        return StoredPin(
            salt = salt.toBase64(),
            hash = hash(pin, salt).toBase64()
        )
    }

    fun verify(pin: String, storedPin: StoredPin): Boolean {
        val salt = storedPin.salt.fromBase64()
        val actualHash = hash(pin, salt)
        return MessageDigest.isEqual(actualHash, storedPin.hash.fromBase64())
    }

    private fun hash(pin: String, salt: ByteArray): ByteArray {
        val chars = pin.toCharArray()
        return try {
            val spec = PBEKeySpec(chars, salt, Iterations, KeyLength)
            SecretKeyFactory.getInstance(Algorithm).generateSecret(spec).encoded
        } finally {
            chars.fill('\u0000')
        }
    }

    private fun ByteArray.toBase64(): String =
        Base64.getEncoder().encodeToString(this)

    private fun String.fromBase64(): ByteArray =
        Base64.getDecoder().decode(this)

    private companion object {
        const val Algorithm = "PBKDF2WithHmacSHA256"
        const val Iterations = 120_000
        const val KeyLength = 256
        const val SaltSize = 16
    }
}
