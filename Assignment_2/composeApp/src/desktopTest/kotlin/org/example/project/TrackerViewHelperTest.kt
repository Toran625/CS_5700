package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class TrackerViewHelperTest {
    private lateinit var helper: TrackerViewHelper

    @BeforeEach
    fun setUp() {
        helper = TrackerViewHelper()
    }

    @Test
    fun `test initial state`() {
        assertEquals("", helper.id)
        assertEquals("", helper.status)
        assertEquals(0L, helper.expectedDeliveryDateTimestamp)
        assertEquals("", helper.currentLocation)
        assertEquals("", helper.shipmentType)
        assertEquals(0L, helper.createdTimestamp)
        assertTrue(helper.notes.isEmpty())
        assertTrue(helper.updateHistory.isEmpty())
    }

    @Test
    fun `test update method updates all fields`() {
        val notes = mutableListOf("Note 1", "Note 2")
        val updates = mutableListOf(
            ShipmentUpdate("created", "shipped", "TEST123", 1234567890L, Created())
        )

        helper.update(
            "TEST123",
            "shipped",
            1234567999L,
            "Transit Hub",
            notes,
            updates,
            "Express",
            1234567890L
        )

        assertEquals("TEST123", helper.id)
        assertEquals("shipped", helper.status)
        assertEquals(1234567999L, helper.expectedDeliveryDateTimestamp)
        assertEquals("Transit Hub", helper.currentLocation)
        assertEquals("Express", helper.shipmentType)
        assertEquals(1234567890L, helper.createdTimestamp)
        assertEquals(2, helper.notes.size)
        assertEquals("Note 1", helper.notes[0])
        assertEquals("Note 2", helper.notes[1])
        assertEquals(1, helper.updateHistory.size)
    }

    @Test
    fun `test update method clears and replaces collections`() {
        // Initial data
        helper.notes.add("Initial note")
        helper.updateHistory.add(ShipmentUpdate("", "initial", "OLD123", 1L, Created()))

        val newNotes = mutableListOf("New note")
        val newUpdates = mutableListOf(
            ShipmentUpdate("created", "shipped", "TEST123", 1234567890L, Shipped())
        )

        helper.update(
            "TEST123",
            "shipped",
            1234567999L,
            "Transit Hub",
            newNotes,
            newUpdates,
            "Express",
            1234567890L
        )

        assertEquals(1, helper.notes.size)
        assertEquals("New note", helper.notes[0])
        assertEquals(1, helper.updateHistory.size)
        assertEquals("shipped", helper.updateHistory[0].updateType)
    }
}