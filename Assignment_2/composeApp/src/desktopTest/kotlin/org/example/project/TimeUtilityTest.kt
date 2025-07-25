package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimeUtilityTest {

    @Test
    fun `test formatTimestamp formats correctly`() {
        val timestamp = 1234567890000L // January 1, 2009 (approximately)
        val formatted = formatTimestamp(timestamp)

        // The exact format depends on system locale and timezone
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
        assertTrue(formatted.contains("2009") || formatted.contains("Jan"))
    }

    @Test
    fun `test formatTimestamp with zero timestamp`() {
        val timestamp = 0L
        val formatted = formatTimestamp(timestamp)

        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun `test formatTimestamp consistency`() {
        val timestamp = 1609459200000L // January 1, 2021
        val formatted1 = formatTimestamp(timestamp)
        val formatted2 = formatTimestamp(timestamp)

        assertEquals(formatted1, formatted2)
    }

    @Test
    fun `test formatTimestamp with current time`() {
        val currentTime = System.currentTimeMillis()
        val formatted = formatTimestamp(currentTime)

        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
        // Should contain current year
        assertTrue(formatted.contains("2024") || formatted.contains("2025"))
    }
}