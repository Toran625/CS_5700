package org.example.project

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TrackingServerTest {
    private lateinit var server: TrackingServer

    @BeforeEach
    fun setUp() {
        server = TrackingServer.instance
    }

    @Test
    fun `test findShipment returns null for non-existent shipment`() {
        assertNull(server.findShipment("NONEXISTENT"))
    }

    @Test
    fun `test addShipment creates standard shipment by default`() {
        val shipment = server.addShipment("TEST123", "created", null, 1234567890L)

        assertEquals("TEST123", shipment.id)
        assertEquals("created", shipment.status)
        assertEquals("Standard", shipment.shipmentType)
        assertEquals("Origin Facility", shipment.currentLocation)
        assertEquals(1234567890L, shipment.createdTimestamp)
        assertEquals(0L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun `test addShipment creates express shipment`() {
        val shipment = server.addShipment("EXP123", "created", "express", 1234567890L)
        assertEquals("Express", shipment.shipmentType)
        assertInstanceOf(ExpressShipment::class.java, shipment)
    }

    @Test
    fun `test addShipment creates overnight shipment`() {
        val shipment = server.addShipment("OVN123", "created", "overnight", 1234567890L)
        assertEquals("Overnight", shipment.shipmentType)
        assertInstanceOf(OvernightShipment::class.java, shipment)
    }

    @Test
    fun `test addShipment creates bulk shipment`() {
        val shipment = server.addShipment("BLK123", "created", "bulk", 1234567890L)
        assertEquals("Bulk", shipment.shipmentType)
        assertInstanceOf(BulkShipment::class.java, shipment)
    }

    @Test
    fun `test addShipment with unknown type creates standard shipment`() {
        val shipment = server.addShipment("UNK123", "created", "unknown", 1234567890L)
        assertEquals("Standard", shipment.shipmentType)
        assertInstanceOf(StandardShipment::class.java, shipment)
    }

    @Test
    fun `test addShipment stores shipment in map`() {
        val shipment = server.addShipment("TEST123", "created", null, 1234567890L)

        assertEquals(shipment, server.findShipment("TEST123"))
    }

    @Test
    fun `test addShipment with case insensitive type matching`() {
        val expressShipment = server.addShipment("EXP1", "created", "EXPRESS", 1234567890L)
        val bulkShipment = server.addShipment("BLK1", "created", "BULK", 1234567890L)
        val overnightShipment = server.addShipment("OVN1", "created", "OVERNIGHT", 1234567890L)

        assertEquals("Express", expressShipment.shipmentType)
        assertEquals("Bulk", bulkShipment.shipmentType)
        assertEquals("Overnight", overnightShipment.shipmentType)
    }

    @Test
    fun `test multiple shipments can be stored`() {
        val shipment1 = server.addShipment("TEST1", "created", null, 1234567890L)
        val shipment2 = server.addShipment("TEST2", "created", "express", 1234567891L)

        assertEquals(shipment1, server.findShipment("TEST1"))
        assertEquals(shipment2, server.findShipment("TEST2"))
        assertNotEquals(shipment1, shipment2)
    }
}