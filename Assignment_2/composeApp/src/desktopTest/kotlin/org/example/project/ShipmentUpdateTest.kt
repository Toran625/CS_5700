package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class ShipmentUpdateTest {
    private lateinit var shipment: StandardShipment
    private lateinit var mockMethod: UpdateMethod

    @BeforeEach
    fun setUp() {
        shipment = StandardShipment("TEST123", "created", 1234567890L, "Origin", 1234567890L)
        mockMethod = mock()
    }

    @Test
    fun `test ShipmentUpdate creation`() {
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567891L, mockMethod, "extra")

        assertEquals("created", update.previousStatus)
        assertEquals("shipped", update.updateType)
        assertEquals("TEST123", update.shipmentId)
        assertEquals(1234567891L, update.timestamp)
        assertEquals(mockMethod, update.method)
        assertEquals("extra", update.otherInfo)
    }

    @Test
    fun `test ShipmentUpdate creation without otherInfo`() {
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567891L, mockMethod)

        assertNull(update.otherInfo)
    }

    @Test
    fun `test applyToShipment calls method and adds update`() {
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567891L, mockMethod)

        update.applyToShipment(shipment)

        verify(mockMethod).processInfo(eq(update), eq(shipment))
        assertEquals(1, shipment.updateHistory.size)
        assertEquals(update, shipment.updateHistory[0])
    }
}