package org.example.project

import kotlin.test.*
import kotlinx.coroutines.runBlocking

class TrackingSimulatorTest {

    private lateinit var simulator: TrackingSimulator

    @BeforeTest
    fun setup() {
        simulator = TrackingSimulator("test_input.txt")
    }

    @Test
    fun testRunSimulation() = runBlocking {
        try {
            simulator.runSimulation()
            fail("Expected IllegalArgumentException for 'invalidupdate' line")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Unknown update method"), "Unexpected exception message: ${e.message}")
        }

        val shipment = simulator.findShipment("sTest01")
        assertNotNull(shipment)

        assertEquals("canceled", shipment.status) // This should be the final valid status before the exception

        assertTrue(shipment.notes.contains("test note"))
        assertEquals("Los Angeles CA", shipment.currentLocation)
    }

    @Test
    fun testAddShipment() {
        val update = ShipmentUpdate(
            previousStatus = "N/A",
            updateType = "created",
            shipmentId = "s12345",
            timestamp = 1234567890,
            method = Created(),
            otherInfo = "Origin Facility"
        )

        val shipment = simulator.addShipment(update)

        assertEquals("s12345", shipment.id)
        assertEquals("created", shipment.status)
        assertEquals("Origin Facility", shipment.currentLocation)
    }

    @Test
    fun testFindShipment() {
        val update = ShipmentUpdate(
            previousStatus = "N/A",
            updateType = "created",
            shipmentId = "s54321",
            timestamp = 1234567890,
            method = Created(),
            otherInfo = "LA"
        )

        simulator.addShipment(update)
        val found = simulator.findShipment("s54321")

        assertNotNull(found)
        assertEquals("s54321", found?.id)
    }
}
