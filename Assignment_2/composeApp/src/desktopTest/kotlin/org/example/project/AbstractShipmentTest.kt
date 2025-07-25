package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class TestShipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String,
    createdTimestamp: Long
) : Shipment(id, status, expectedDeliveryDateTimestamp, currentLocation, createdTimestamp) {
    override val shipmentType: String = "Test"

    override fun receiveUpdate(
        updateType: String,
        shipmentId: String,
        timestamp: Long,
        otherInfo: String?,
        method: UpdateMethod
    ) {
        val previousStatus = this.status
        val update = ShipmentUpdate(previousStatus, updateType, shipmentId, timestamp, method, otherInfo)
        update.applyToShipment(this)
    }
}

class AbstractShipmentTest {
    private lateinit var shipment: TestShipment
    private lateinit var mockObserver: ShipmentObserver

    @BeforeEach
    fun setUp() {
        shipment = TestShipment("TEST123", "created", 1234567890L, "Origin", 1234567890L)
        mockObserver = mock()
    }

    @Test
    fun `test shipment initialization`() {
        assertEquals("TEST123", shipment.id)
        assertEquals("created", shipment.status)
        assertEquals(1234567890L, shipment.expectedDeliveryDateTimestamp)
        assertEquals("Origin", shipment.currentLocation)
        assertEquals("Test", shipment.shipmentType)
        assertTrue(shipment.notes.isEmpty())
        assertTrue(shipment.updateHistory.isEmpty())
    }

    @Test
    fun `test addNote adds note and notifies observers`() {
        shipment.addObserver(mockObserver)

        shipment.addNote("Test note")

        assertEquals(1, shipment.notes.size)
        assertEquals("Test note", shipment.notes[0])
        verify(mockObserver, times(2)).update(any(), any(), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun `test addUpdate adds update and notifies observers`() {
        shipment.addObserver(mockObserver)
        val update = ShipmentUpdate("created", "shipped", "TEST123", 1234567891L, Created())

        shipment.addUpdate(update)

        assertEquals(1, shipment.updateHistory.size)
        assertEquals(update, shipment.updateHistory[0])
        verify(mockObserver, times(2)).update(any(), any(), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun `test addObserver calls update immediately`() {
        shipment.addObserver(mockObserver)

        verify(mockObserver).update(
            eq("TEST123"),
            eq("created"),
            eq(1234567890L),
            eq("Origin"),
            eq(shipment.notes),
            eq(shipment.updateHistory),
            eq("Test"),
            eq(1234567890L)
        )
    }

    @Test
    fun `test removeObserver stops notifications`() {
        shipment.addObserver(mockObserver)
        shipment.removeObserver(mockObserver)

        shipment.addNote("Test note")

        verify(mockObserver, times(1)).update(any(), any(), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun `test receiveUpdate processes update correctly`() {
        val mockMethod = mock<UpdateMethod>()

        shipment.receiveUpdate("shipped", "TEST123", 1234567891L, "extra info", mockMethod)

        assertEquals(1, shipment.updateHistory.size)
        val update = shipment.updateHistory[0]
        assertEquals("created", update.previousStatus)
        assertEquals("shipped", update.updateType)
        assertEquals("TEST123", update.shipmentId)
        assertEquals(1234567891L, update.timestamp)
        assertEquals("extra info", update.otherInfo)
        verify(mockMethod).processInfo(eq(update), eq(shipment))
    }

    @Test
    fun `test multiple observers receive notifications`() {
        val observer1 = mock<ShipmentObserver>()
        val observer2 = mock<ShipmentObserver>()

        shipment.addObserver(observer1)
        shipment.addObserver(observer2)

        shipment.addNote("Test note")

        verify(observer1, times(2)).update(any(), any(), any(), any(), any(), any(), any(), any())
        verify(observer2, times(2)).update(any(), any(), any(), any(), any(), any(), any(), any())
    }
}