package org.example.project

import kotlin.test.*

class UpdateMethodTest {

    @Test
    fun testCreatedUpdate() {
        val shipment = Shipment("s1", "init", 0L, "nowhere")
        val update = ShipmentUpdate("init", "created", "s1", 0L, Created())
        update.applyToShipment(shipment)

        assertEquals("created", shipment.status)
    }

    @Test
    fun testShippedUpdateSetsStatusAndDate() {
        val shipment = Shipment("s2", "created", 0L, "origin")
        val expectedTimestamp = 1650000000000
        val update = ShipmentUpdate("created", "shipped", "s2", 0L, Shipped(), expectedTimestamp.toString())

        update.applyToShipment(shipment)

        assertEquals("shipped", shipment.status)
        assertEquals(expectedTimestamp, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun testLocationUpdateSetsCurrentLocation() {
        val shipment = Shipment("s3", "shipped", 0L, "origin")
        val update = ShipmentUpdate("shipped", "location", "s3", 0L, Location(), "Denver CO")

        update.applyToShipment(shipment)

        assertEquals("Denver CO", shipment.currentLocation)
    }

    @Test
    fun testNoteAddedAddsNote() {
        val shipment = Shipment("s4", "shipped", 0L, "Denver")
        val update = ShipmentUpdate("shipped", "noteadded", "s4", 0L, NoteAdded(), "Box torn")

        update.applyToShipment(shipment)

        assertTrue("Box torn" in shipment.notes)
    }

    @Test
    fun testDelayedSetsTimestamp() {
        val shipment = Shipment("s5", "shipped", 0L, "Denver")
        val delayTimestamp = 1650001112222
        val update = ShipmentUpdate("shipped", "delayed", "s5", 0L, Delayed(), delayTimestamp.toString())

        update.applyToShipment(shipment)

        assertEquals("delayed", shipment.status)
        assertEquals(delayTimestamp, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun testCanceledUpdate() {
        val shipment = Shipment("s123", "created", 0, "Warehouse")
        val update = ShipmentUpdate(
            previousStatus = shipment.status,
            updateType = "canceled",
            shipmentId = "s123",
            timestamp = System.currentTimeMillis(),
            method = Canceled(),
            otherInfo = null
        )

        update.applyToShipment(shipment)

        assertEquals("canceled", shipment.status)
    }

    @Test
    fun testDeliveredUpdate() {
        val shipment = Shipment("s123", "shipped", 0, "On the way")
        val update = ShipmentUpdate(
            previousStatus = shipment.status,
            updateType = "delivered",
            shipmentId = "s123",
            timestamp = System.currentTimeMillis(),
            method = Delivered(),
            otherInfo = null
        )

        update.applyToShipment(shipment)

        assertEquals("delivered", shipment.status)
    }

    @Test
    fun testLostUpdate() {
        val shipment = Shipment("s123", "shipped", 0, "Unknown")
        val update = ShipmentUpdate(
            previousStatus = shipment.status,
            updateType = "lost",
            shipmentId = "s123",
            timestamp = System.currentTimeMillis(),
            method = Lost(),
            otherInfo = null
        )

        update.applyToShipment(shipment)

        assertEquals("lost", shipment.status)
    }
}
