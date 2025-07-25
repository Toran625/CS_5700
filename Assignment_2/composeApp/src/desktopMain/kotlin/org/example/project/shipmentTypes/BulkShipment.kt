package org.example.project

class BulkShipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String,
    createdTimestamp: Long
) : Shipment(id, status, expectedDeliveryDateTimestamp, currentLocation, createdTimestamp) {

    override val shipmentType = "Bulk"

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

        println("${expectedDeliveryDateTimestamp} before apply")

        update.applyToShipment(this)

        if (expectedDeliveryDateTimestamp != 0L) {
            val threeDaysAfterCreation = createdTimestamp + (3 * 24 * 60 * 60 * 1000)
            println("${expectedDeliveryDateTimestamp} after apply, before check")
            if (expectedDeliveryDateTimestamp < threeDaysAfterCreation &&updateType.lowercase() != "delayed") {
                addNote("A bulk shipment was updated with a delivery date too soon after creation.")
            }
        }
    }
}