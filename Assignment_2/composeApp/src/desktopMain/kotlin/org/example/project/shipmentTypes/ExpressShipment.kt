package org.example.project

class ExpressShipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String,
    createdTimestamp: Long
) : Shipment(id, status, expectedDeliveryDateTimestamp, currentLocation, createdTimestamp) {

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

        val threeDaysLater = createdTimestamp + (3 * 24 * 60 * 60 * 1000)
        if (expectedDeliveryDateTimestamp > threeDaysLater && updateType.lowercase() != "delayed") {
            addNote("An express shipment was updated with a delivery date more than 3 days after creation.")
        }
    }
}