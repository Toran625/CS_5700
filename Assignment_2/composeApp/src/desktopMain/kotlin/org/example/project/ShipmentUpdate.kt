package org.example.project

data class ShipmentUpdate(
    val updateType: String,
    val shipmentId: String,
    val timestamp: Long,
    val method: UpdateMethod,
    val otherInfo: String? = null
) {
    fun applyToShipment(shipment: Shipment) {
        method.processInfo(this, shipment)
    }
}
