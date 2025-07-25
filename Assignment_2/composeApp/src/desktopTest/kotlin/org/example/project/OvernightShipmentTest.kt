package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class OvernightShipmentTest {
    private lateinit var shipment: OvernightShipment

    @BeforeEach
    fun setUp() {
        shipment = OvernightShipment("OVN123", "created", 0L, "Origin Facility", 1234567890L)
    }

    @Test
    fun `test shipment type is Overnight`() {
        assertEquals("Overnight", shipment.shipmentType)
    }

    @Test
    fun `test delivery date within 24 hours does not add note`() {
        val twelveHoursLater = shipment.createdTimestamp + (12 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "OVN123", 1234567891L, twelveHoursLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(twelveHoursLater, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test delivery date after 24 hours adds warning note`() {
        val twoDaysLater = shipment.createdTimestamp + (2 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "OVN123", 1234567891L, twoDaysLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(twoDaysLater, shipment.expectedDeliveryDateTimestamp)
        assertEquals(1, shipment.notes.size)
        assertEquals("An overnight shipment was updated to include a delivery date later than 24 hours after it was created.", shipment.notes[0])
    }

    @Test
    fun `test delayed update does not add warning note`() {
        val twoDaysLater = shipment.createdTimestamp + (2 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("delayed", "OVN123", 1234567891L, twoDaysLater.toString(), Delayed())

        assertEquals("delayed", shipment.status)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test exactly 24 hours later does not add note`() {
        val exactly24HoursLater = shipment.createdTimestamp + (24 * 60 * 60 * 1000)

        shipment.receiveUpdate("shipped", "OVN123", 1234567891L, exactly24HoursLater.toString(), Shipped())

        assertEquals("shipped", shipment.status)
        assertTrue(shipment.notes.isEmpty())
    }

    @Test
    fun `test case insensitive delayed check`() {
        val twoDaysLater = shipment.createdTimestamp + (2 * 24 * 60 * 60 * 1000)

        shipment.receiveUpdate("DeLaYeD", "OVN123", 1234567891L, twoDaysLater.toString(), Delayed())

        assertTrue(shipment.notes.isEmpty())
    }
}