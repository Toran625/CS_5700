package org.example.project

import kotlin.test.*

class ShipmentTest {

    @Test
    fun testAddNote() {
        val shipment = Shipment("s100", "created", 0L, "Warehouse")
        shipment.addNote("This is a test note")

        assertTrue(shipment.notes.contains("This is a test note"))
    }


    @Test
    fun testAddObserverAndNotify() {
        val shipment = Shipment("s200", "created", 0L, "Origin")

        shipment.addNote("first note")

        val shipmentUpdate = ShipmentUpdate("created", "shipped", "s200", 123456L, Shipped(), "456789")
        shipmentUpdate.applyToShipment(shipment)

        val tracker = TrackerViewHelper()
        shipment.addObserver(tracker)

        assertEquals("s200", tracker.id)
        assertEquals("shipped", tracker.status)
        assertEquals("Origin", tracker.currentLocation)
        assertEquals(listOf("first note"), tracker.notes)
        assertEquals(1, tracker.updateHistory.size)
    }

    @Test
    fun testRemoveObserverPreventsUpdate() {
        val shipment = Shipment("s123", "created", 0, "NY")
        val tracker = TrackerViewHelper()

        shipment.addObserver(tracker)
        shipment.removeObserver(tracker)

        val originalStatus = tracker.status
        val originalLocation = tracker.currentLocation
        val originalNotes = tracker.notes.toList()
        val originalHistorySize = tracker.updateHistory.size

        val update = ShipmentUpdate(
            previousStatus = "created",
            updateType = "delivered",
            shipmentId = "s123",
            timestamp = System.currentTimeMillis(),
            method = Delivered(),
            otherInfo = null
        )
        update.applyToShipment(shipment)

        assertEquals(originalStatus, tracker.status)
        assertEquals(originalLocation, tracker.currentLocation)
        assertEquals(originalNotes, tracker.notes)
        assertEquals(originalHistorySize, tracker.updateHistory.size)
    }
}
