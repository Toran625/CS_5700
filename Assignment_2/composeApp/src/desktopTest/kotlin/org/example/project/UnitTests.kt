package org.example.project

import kotlin.test.*

class UnitTests{

    @Test
    fun main() {
        println("Running TrackingSimulator tests...")
        runTrackingSimulatorTests()

        println("Running Shipment tests...")
        runShipmentTests()

        println("Running UpdateMethod tests...")
        runUpdateMethodTests()

        println("✅ All unit tests completed.")
    }


    fun runTrackingSimulatorTests() {
        val test = TrackingSimulatorTest()
        test.setup()
        test.testRunSimulation()
        test.testAddShipment()
        test.testFindShipment()
    }

    fun runShipmentTests() {
        val test = ShipmentTest()
        test.testAddNote()
        test.testAddObserverAndNotify()
        test.testRemoveObserverPreventsUpdate()
    }

    fun runUpdateMethodTests() {
        val test = UpdateMethodTest()
        test.testCreatedUpdate()
        test.testShippedUpdateSetsStatusAndDate()
        test.testLocationUpdateSetsCurrentLocation()
        test.testNoteAddedAddsNote()
        test.testDelayedSetsTimestamp()
        test.testCanceledUpdate()
        test.testLostUpdate()
        test.testDeliveredUpdate()
    }
}

