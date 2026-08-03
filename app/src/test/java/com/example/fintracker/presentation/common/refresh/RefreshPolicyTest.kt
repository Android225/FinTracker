package com.example.fintracker.presentation.common.refresh

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class RefreshPolicyTest {
    @Test
    fun `automatic refresh waits until successful data becomes stale`() {
        val clock = MutableClock(Instant.EPOCH)
        val policy = RefreshPolicy(clock, staleAfterMillis = 30_000L)

        assertTrue(policy.shouldRefresh(force = false))
        policy.markSuccessfulRefresh()
        assertFalse(policy.shouldRefresh(force = false))

        clock.advanceBy(29_999L)
        assertFalse(policy.shouldRefresh(force = false))
        clock.advanceBy(1L)
        assertTrue(policy.shouldRefresh(force = false))
    }

    @Test
    fun `manual refresh always bypasses freshness interval`() {
        val policy = RefreshPolicy(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC))
        policy.markSuccessfulRefresh()

        assertTrue(policy.shouldRefresh(force = true))
    }

    private class MutableClock(
        private var currentInstant: Instant,
        private val zoneId: ZoneId = ZoneOffset.UTC
    ) : Clock() {
        override fun getZone(): ZoneId = zoneId

        override fun withZone(zone: ZoneId): Clock = MutableClock(currentInstant, zone)

        override fun instant(): Instant = currentInstant

        fun advanceBy(millis: Long) {
            currentInstant = currentInstant.plusMillis(millis)
        }
    }
}
