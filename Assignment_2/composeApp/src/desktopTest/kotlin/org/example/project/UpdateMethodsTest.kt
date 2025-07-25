package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class UpdateMethodsTest {
    private lateinit var shipment: StandardShipment

    @BeforeEach
    fun setUp() {
        shipment = StandardShipment("TEST123", "created", 0L, "Origin", 1234567890L)
    }

    @Test
    fun `test Created processInfo`() {
        val method = Created()
        val update = ShipmentUpdate("", "created", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("created", shipment.status)
    }

    @Test
    fun `test Shipped processInfo with delivery date`() {
        val method = Shipped()
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567890L, method, "1234567999")

        method.processInfo(update, shipment)

        assertEquals("shipped", shipment.status)
        assertEquals(1234567999L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test Shipped processInfo without delivery date`() {
        val method = Shipped()
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test Shipped processInfo with invalid delivery date`() {
        val method = Shipped()
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567890L, method, "invalid")

        method.processInfo(update, shipment)

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp) // Should remain unchanged
    }

    @Test
    fun `test Location processInfo`() {
        val method = Location()
        val update = ShipmentUpdate("shipped", "location", "TEST123", 1234567890L, method, "Transit Hub")

        method.processInfo(update, shipment)

        assertEquals("Transit Hub", shipment.currentLocation)
    }

    @Test
    fun `test Location processInfo without location`() {
        val originalLocation = shipment.currentLocation
        val method = Location()
        val update = ShipmentUpdate("shipped", "location", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals(originalLocation, shipment.currentLocation)
    }

    @Test
    fun `test Delivered processInfo`() {
        val method = Delivered()
        val update = ShipmentUpdate("shipped", "delivered", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("delivered", shipment.status)
    }

    @Test
    fun `test Delayed processInfo with new delivery date`() {
        val method = Delayed()
        val update = ShipmentUpdate("shipped", "delayed", "TEST123", 1234567890L, method, "1234567999")

        method.processInfo(update, shipment)

        assertEquals("delayed", shipment.status)
        assertEquals(1234567999L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test Delayed processInfo without new delivery date`() {
        val method = Delayed()
        val update = ShipmentUpdate("shipped", "delayed", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("delayed", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test Delayed processInfo with invalid delivery date`() {
        val method = Delayed()
        val update = ShipmentUpdate("shipped", "delayed", "TEST123", 1234567890L, method, "not_a_number")

        method.processInfo(update, shipment)

        assertEquals("delayed", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test Lost processInfo`() {
        val method = Lost()
        val update = ShipmentUpdate("shipped", "lost", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("lost", shipment.status)
    }

    @Test
    fun `test Canceled processInfo`() {
        val method = Canceled()
        val update = ShipmentUpdate("shipped", "canceled", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertEquals("canceled", shipment.status)
    }

    @Test
    fun `test NoteAdded processInfo`() {
        val method = NoteAdded()
        val update = ShipmentUpdate("shipped", "noteadded", "TEST123", 1234567890L, method, "Important note")

        method.processInfo(update, shipment)

        assertEquals(1, shipment.notes.size)
        assertEquals("Important note", shipment.notes[0])
    }

    @Test
    fun `test NoteAdded processInfo without note`() {
        val method = NoteAdded()
        val update = ShipmentUpdate("shipped", "noteadded", "TEST123", 1234567890L, method)

        method.processInfo(update, shipment)

        assertTrue(shipment.notes.isEmpty())
    }
}