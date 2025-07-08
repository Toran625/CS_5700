package org.example.project

data class ShipmentUpdate(
    var updateType: String,
    var shipmentId: String,
    var timestamp: Long,
    var method: UpdateMethod,
    var otherInfo: String? = null
) {
    fun applyToShipment(shipment: Shipment) {
        method.processInfo(this, shipment)
        shipment.addUpdate(this)
    }
}
