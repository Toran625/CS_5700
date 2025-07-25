package org.example.project

class OvernightShipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String,
    createdTimestamp: Long
) : Shipment(id, status, expectedDeliveryDateTimestamp, currentLocation, createdTimestamp) {

    override val shipmentType = "Overnight"

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

        val oneDayLater = createdTimestamp + (24 * 60 * 60 * 1000)
        if (expectedDeliveryDateTimestamp > oneDayLater && updateType.lowercase() != "delayed") {
            addNote("An overnight shipment was updated to include a delivery date later than 24 hours after it was created.")
        }
    }
}