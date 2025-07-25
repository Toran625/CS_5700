package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class ExpressShipmentTest {
    private lateinit var shipment: ExpressShipment

    @BeforeEach
    fun setUp() {
        shipment = ExpressShipment("EXP123", "created", 0L, "Origin Facility", 1234567890L)
    }

    @Test
    fun `test shipment type is Express`() {
        assertEquals("Express", shipment.shipmentType)
    }

    @Test
    fun `test delivery date within 3 days does not add note`() {
        val twoDaysLater = shipment.createdTimestamp + (2 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "EXP123", 1234567891L, twoDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(twoDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test delivery date after 3 days adds warning note`() {
        val fourDaysLater = shipment.createdTimestamp + (4 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "EXP123", 1234567891L, fourDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(fourDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertEquals(1, shipment.notes.size)
        assertEquals("An express shipment was updated with a delivery date more than 3 days after creation.", shipment.notes[0])
    }

    @Test
    fun `test delayed update does not add warning note even with late delivery`() {
        val fourDaysLater = shipment.createdTimestamp + (4 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("delayed", "EXP123", 1234567891L, fourDaysLater.toString(), Delayed())

        assertEquals("delayed", shipment.status)
        assertEquals(fourDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty()) // No warning for delayed updates
    }

    @Test
    fun `test exactly 3 days later does not add note`() {
        val exactlyThreeDaysLater = shipment.createdTimestamp + (3 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "EXP123", 1234567891L, exactlyThreeDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test case insensitive delayed check`() {
        val fourDaysLater = shipment.createdTimestamp + (4 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("DELAYED", "EXP123", 1234567891L, fourDaysLater.toString(), Delayed())

        assertTrue(shipment.notes.isEmpty()) // Should still work with uppercase
    }
}