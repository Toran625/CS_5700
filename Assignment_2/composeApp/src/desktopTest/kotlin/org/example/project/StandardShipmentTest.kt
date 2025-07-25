package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class StandardShipmentTest {
    private lateinit var shipment: StandardShipment

    @BeforeEach
    fun setUp() {
        shipment = StandardShipment("STD123", "created", 0L, "Origin Facility", 1234567890L)
    }

    @Test
    fun `test shipment type is Standard`() {
        assertEquals("Standard", shipment.shipmentType)
    }

    @Test
    fun `test receiveUpdate with shipped status`() {
        shipment.receiveUpdate("shipped", "STD123", 1234567891L, "1234567999", Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(1234567999L, shipment.expectedDeliveryDateTimestamp)
        assertEquals(1, shipment.updateHistory.size)
    }

    @Test
    fun `test receiveUpdate with location update`() {
        shipment.receiveUpdate("location", "STD123", 1234567891L, "Transit Hub", Location())

        assertEquals("Transit Hub", shipment.currentLocation)
        assertEquals(1, shipment.updateHistory.size)
    }

    @Test
    fun `test receiveUpdate with delivered status`() {
        shipment.receiveUpdate("delivered", "STD123", 1234567891L, null, Delivered())

        assertEquals("delivered", shipment.status)
        assertEquals(1, shipment.updateHistory.size)
    }

    @Test
    fun `test standard shipment has no special validation rules`() {
        // Standard shipments don't add automatic notes for delivery dates
        shipment.receiveUpdate("shipped", "STD123", 1234567891L, "9999999999999", Shipped())

        assertEquals("shipped", shipment.status)
        assertEquals(9999999999999L, shipment.expectedDeliveryDateTimestamp)
        assertTrue(shipment.notes.isEmpty()) // No automatic notes added
    }
}