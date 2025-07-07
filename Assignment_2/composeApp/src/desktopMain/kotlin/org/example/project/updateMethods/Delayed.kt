class Delayed : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        shipment.status = update.updateType
        update.otherInfo?.toLongOrNull()?.let {
            shipment.expectedDeliveryDateTimestamp = it
        }
    }
}
