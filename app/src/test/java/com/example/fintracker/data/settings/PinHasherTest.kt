package com.example.fintracker.data.settings

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHasherTest {
    private val hasher = PinHasher()

    @Test
    fun `created hash accepts original pin`() {
        val storedPin = hasher.create("4826")

        assertTrue(hasher.verify("4826", storedPin))
    }

    @Test
    fun `created hash rejects another pin`() {
        val storedPin = hasher.create("4826")

        assertFalse(hasher.verify("4827", storedPin))
    }

    @Test
    fun `same pin receives different salted hashes`() {
        val first = hasher.create("4826")
        val second = hasher.create("4826")

        assertNotEquals(first.salt, second.salt)
        assertNotEquals(first.hash, second.hash)
    }
}
