package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class BulkShipmentTest {
    private lateinit var shipment: BulkShipment

    @BeforeEach
    fun setUp() {
        shipment = BulkShipment("BLK123", "created", 0L, "Origin Facility", 1234567890L)
    }

    @Test
    fun `test shipment type is Bulk`() {
        assertEquals("Bulk", shipment.shipmentType)
    }

    @Test
    fun `test delivery date after 3 days does not add note`() {
        val fourDaysLater = shipment.createdTimestamp + (4 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "BLK123", 1234567891L, fourDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(fourDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test delivery date within 3 days adds warning note`() {
        val twoDaysLater = shipment.createdTimestamp + (2 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "BLK123", 1234567891L, twoDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(twoDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertEquals(1, shipment.notes.size)
        assertEquals("A bulk shipment was updated with a delivery date too soon after creation.", shipment.notes[0])
    }

    @Test
    fun `test delayed update does not add warning note even with early delivery`() {
        val oneDayLater = shipment.createdTimestamp + (1 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("delayed", "BLK123", 1234567891L, oneDayLater.toString(), Delayed())

        assertEquals("delayed", shipment.status)
        assertEquals(oneDayLater, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test exactly 3 days later does not add note`() {
        val exactlyThreeDaysLater = shipment.createdTimestamp + (3 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "BLK123", 1234567891L, exactlyThreeDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test zero expected delivery date does not add note`() {
        // When expectedDeliveryDateTimestamp is 0, no note should be added
        shipment.receiveUpdate("shipped", "BLK123", 1234567891L, null, Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test case insensitive delayed check`() {
        val oneDayLater = shipment.createdTimestamp + (1 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("DELAYED", "BLK123", 1234567891L, oneDayLater.toString(), Delayed())

        assertTrue(shipment.notes.isEmpty())
    }
}