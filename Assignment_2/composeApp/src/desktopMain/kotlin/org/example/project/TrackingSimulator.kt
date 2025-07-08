package org.example.project

import kotlinx.coroutines.*
import java.io.*


class TrackingSimulator(private val filepath: String) {
    private val shipments = mutableListOf<Shipment>()

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) = shipments.add(shipment)
    

    suspend fun runSimulation() = coroutineScope{
        launch {
            File(filepath).useLines { lines ->
                lines.forEach { line ->
                    parseAndApplyUpdate(line)
                    delay(1000)
                }
            }
        }
    }

    private fun parseAndApplyUpdate(line: String) {
        val parts = line.split(",", limit = 4)

        val updateType = parts[0]
        val shipmentId = parts[1]
        val timestamp = parts[2].toLongOrNull() ?: return
        val otherInfo = parts.getOrNull(3)

        val method = getUpdateMethodFor(updateType)

        val update = ShipmentUpdate(
            updateType = updateType,
            shipmentId = shipmentId,
            timestamp = timestamp,
            method = method,
            otherInfo = otherInfo
        )

        val existingShipment = findShipment(shipmentId)

        val shipment = if (updateType.lowercase() == "created" && method is Created && existingShipment == null) {
            createShipment(update).also { addShipment(it) }
        } else {
            existingShipment
        }

        if (shipment != null){
            shipment.addUpdate(update)
            update.applyToShipment(shipment)
        }
    }


    private fun getUpdateMethodFor(status: String): UpdateMethod {
        return when (status.lowercase()) {
            "created" -> Created()
            "shipped" -> Shipped()
            "location" -> Location()
            "delivered" -> Delivered()
            "delayed" -> Delayed()
            "lost" -> Lost()
            "canceled" -> Canceled()
            "noteadded" -> NoteAdded()
            else -> throw IllegalArgumentException("Unknown update method: $status")
        }
    }

    private fun createShipment(update: ShipmentUpdate): Shipment {
        return Shipment(
            id = update.shipmentId,
            status = update.updateType,
            expectedDeliveryDateTimestamp = 0,
            currentLocation = update.otherInfo ?: "Origin Facility"
        )
    }
    
}
