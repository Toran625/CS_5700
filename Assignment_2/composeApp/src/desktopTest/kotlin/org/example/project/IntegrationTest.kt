package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

class IntegrationTest {
    private lateinit var server: TrackingServer
    private lateinit var helper: TrackerViewHelper

    @BeforeEach
    fun setUp() {
        server = TrackingServer()
        helper = TrackerViewHelper()
    }

    @Test
    fun `test complete standard shipment lifecycle`() {
        // Create shipment
        val shipment = server.addShipment("INT123", "created", null, 1234567890L)
        shipment.addObserver(helper)

        // Verify initial state
        assertEquals("INT123", helper.id)
        assertEquals("created", helper.status)
        assertEquals("Standard", helper.shipmentType)

        // Ship the package
        shipment.receiveUpdate("shipped", "INT123", 1234567891L, "1234567999", Shipped())

        assertEquals("shipped", helper.status)
        assertEquals(1234567999L, helper.expectedDeliveryDateTimestamp)
        assertEquals(1, helper.updateHistory.size)

        // Update location
        shipment.receiveUpdate("location", "INT123", 1234567892L, "Transit Hub", Location())

        assertEquals("Transit Hub", helper.currentLocation)
        assertEquals(2, helper.updateHistory.size)

        // Add note
        shipment.receiveUpdate("noteadded", "INT123", 1234567893L, "Package is fragile", NoteAdded())

        assertEquals(1, helper.notes.size)
        assertEquals("Package is fragile", helper.notes[0])

        // Deliver package
        shipment.receiveUpdate("delivered", "INT123", 1234567894L, null, Delivered())

        assertEquals("delivered", helper.status)
        assertEquals(4, helper.updateHistory.size)
    }

    @Test
    fun `test express shipment with delivery warning`() {
        val shipment = server.addShipment("EXP123", "created", "express", 1234567890L)
        shipment.addObserver(helper)

        // Ship with delivery date more than 3 days later
        val fourDaysLater = 1234567890L + (4 * 24 * 60 * 60 * 1000)
        shipment.receiveUpdate("shipped", "EXP123", 1234567891L, fourDaysLater.toString(), Shipped())

        assertEquals("Express", helper.shipmentType)
        assertEquals("shipped", helper.status)
        assertEquals(1, helper.notes.size)
        assertEquals("An express shipment was updated with a delivery date more than 3 days after creation.", helper.notes[0])
    }

    @Test
    fun `test overnight shipment with delivery warning`() {
        val shipment = server.addShipment("OVN123", "created", "overnight", 1234567890L)
        shipment.addObserver(helper)

        // Ship with delivery date more than 24 hours later
        val twoDaysLater = 1234567890L + (2 * 24 * 60 * 60 * 1000)
        shipment.receiveUpdate("shipped", "OVN123", 1234567891L, twoDaysLater.toString(), Shipped())

        assertEquals("Overnight", helper.shipmentType)
        assertEquals("shipped", helper.status)
        assertEquals(1, helper.notes.size)
        assertEquals("An overnight shipment was updated to include a delivery date later than 24 hours after it was created.", helper.notes[0])
    }

    @Test
    fun `test bulk shipment with early delivery warning`() {
        val shipment = server.addShipment("BLK123", "created", "bulk", 1234567890L)
        shipment.addObserver(helper)

        // Ship with delivery date less than 3 days later
        val twoDaysLater = 1234567890L + (2 * 24 * 60 * 60 * 1000)
        shipment.receiveUpdate("shipped", "BLK123", 1234567891L, twoDaysLater.toString(), Shipped())

        assertEquals("Bulk", helper.shipmentType)
        assertEquals("shipped", helper.status)
        assertEquals(1, helper.notes.size)
        assertEquals("A bulk shipment was updated with a delivery date too soon after creation.", helper.notes[0])
    }

    @Test
    fun `test multiple observers receive updates`() {
        val helper1 = TrackerViewHelper()
        val helper2 = TrackerViewHelper()
        val shipment = server.addShipment("MULTI123", "created", null, 1234567890L)

        shipment.addObserver(helper1)
        shipment.addObserver(helper2)

        shipment.receiveUpdate("shipped", "MULTI123", 1234567891L, null, Shipped())

        assertEquals("shipped", helper1.status)
        assertEquals("shipped", helper2.status)
        assertEquals(1, helper1.updateHistory.size)
        assertEquals(1, helper2.updateHistory.size)
    }

    @Test
    fun `test observer removal stops updates`() {
        val shipment = server.addShipment("REMOVE123", "created", null, 1234567890L)
        shipment.addObserver(helper)

        shipment.receiveUpdate("shipped", "REMOVE123", 1234567891L, null, Shipped())
        assertEquals("shipped", helper.status)

        shipment.removeObserver(helper)

        shipment.receiveUpdate("delivered", "REMOVE123", 1234567892L, null, Delivered())
        assertEquals("shipped", helper.status) // Should not have updated
    }

    @Test
    fun `test shipment with multiple status changes`() {
        val shipment = server.addShipment("STATUS123", "created", "express", 1234567890L)
        shipment.addObserver(helper)

        // Created -> Shipped -> Delayed -> Delivered
        shipment.receiveUpdate("shipped", "STATUS123", 1234567891L, "1234567999", Shipped())
        assertEquals("shipped", helper.status)
        assertEquals(1, helper.updateHistory.size)

        shipment.receiveUpdate("delayed", "STATUS123", 1234567892L, "9999999999", Delayed())
        assertEquals("delayed", helper.status)
        assertEquals(2, helper.updateHistory.size)

        shipment.receiveUpdate("delivered", "STATUS123", 1234567893L, null, Delivered())
        assertEquals("delivered", helper.status)
        assertEquals(3, helper.updateHistory.size)
    }
}