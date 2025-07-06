import kotlinx.coroutines.*
import java.io.*
package updateMethods

class TrackingSimulator(private val filepath: String) {
    private val shipments = mutableListOf<Shipment>()

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) = shipments.add(shipment)
    

    fun runSimulation() {
        GlobalScope.launch {
            File(filepath).useLines { lines ->
                lines.forEach { line ->
                    parseAndApplyUpdate(line)
                    delay(1000)
                }
            }
        }
    }

    private fun parseAndApplyUpdate(line: String) {
        val parts = line.split(",")
        val status = parts[0]
        val shipmentId = parts[1]
        val timestamp = parts[2].toLong()
        if (parts.size == 4) val otherInfo = parts[3] else null

        val shipment = findShipment(shipmentId) ?: return

        val update = ShipmentUpdate(
            previousStatus = shipment.status,
            newStatus = status,
            timestamp = timestamp,
            method = getUpdateMethodFor(status),
            otherInfo = otherInfo
        )

        shipment.addUpdate(update)
        update.applyToShipment(shipment)
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
    
}
