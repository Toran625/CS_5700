package org.example.project

class ExpressShipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String
) : Shipment(id, status, expectedDeliveryDateTimestamp, currentLocation) {

    override val shipmentType = "Express"

    override fun receiveUpdate(
        updateType: String,
        shipmentId: String,
        timestamp: Long,
        otherInfo: String?,
        method: UpdateMethod
    ) {
        val update = ShipmentUpdate(
            previousStatus = this.status,
            updateType = updateType,
            shipmentId = shipmentId,
            timestamp = timestamp,
            method = method,
            otherInfo = otherInfo
        )

        update.applyToShipment(this)
    }
}