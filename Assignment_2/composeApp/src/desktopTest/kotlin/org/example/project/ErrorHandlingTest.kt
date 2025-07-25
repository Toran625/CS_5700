package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class ErrorHandlingTest {
    private lateinit var server: TrackingServer

    @BeforeEach
    fun setUp() {
        server = TrackingServer.instance
    }

    @Test
    fun `test invalid timestamp in delayed update`() {
        val shipment = server.addShipment("ERR123", "created", null, 1234567890L)
        val originalDelivery = shipment.expectedDeliveryDateTimestamp

        // Invalid timestamp should not update delivery date
        shipment.receiveUpdate("delayed", "ERR123", 1234567891L, "invalid_timestamp", Delayed())

        assertEquals("delayed", shipment.status)
        assertEquals(originalDelivery, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test invalid timestamp in shipped update`() {
        val shipment = server.addShipment("ERR124", "created", null, 1234567890L)
        val originalDelivery = shipment.expectedDeliveryDateTimestamp

        shipment.receiveUpdate("shipped", "ERR124", 1234567891L, "not_a_number", Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(originalDelivery, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test empty location update`() {
        val shipment = server.addShipment("ERR125", "created", null, 1234567890L)

        shipment.receiveUpdate("location", "ERR125", 1234567891L, "", Location())

        assertEquals("", shipment.currentLocation)
    }

    @Test
    fun `test null other info handling`() {
        val shipment = server.addShipment("ERR126", "created", null, 1234567890L)

        // These should not throw exceptions
        assertDoesNotThrow {
            shipment.receiveUpdate("location", "ERR126", 1234567891L, null, Location())
            shipment.receiveUpdate("noteadded", "ERR126", 1234567892L, null, NoteAdded())
            shipment.receiveUpdate("delayed", "ERR126", 1234567893L, null, Delayed())
            shipment.receiveUpdate("shipped", "ERR126", 1234567894L, null, Shipped())
        }
    }

    @Test
    fun `test bulk shipment with zero expected delivery does not add note`() {
        val shipment = server.addShipment("BULK_ERR", "created", "bulk", 1234567890L)

        // This should not add a note because expectedDeliveryDateTimestamp remains 0
        shipment.receiveUpdate("shipped", "BULK_ERR", 1234567891L, null, Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test express shipment with zero expected delivery does not add note`() {
        val shipment = server.addShipment("EXP_ERR", "created", "express", 1234567890L)

        shipment.receiveUpdate("shipped", "EXP_ERR", 1234567891L, null, Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test overnight shipment with zero expected delivery does not add note`() {
        val shipment = server.addShipment("OVN_ERR", "created", "overnight", 1234567890L)

        shipment.receiveUpdate("shipped", "OVN_ERR", 1234567891L, null, Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }
}