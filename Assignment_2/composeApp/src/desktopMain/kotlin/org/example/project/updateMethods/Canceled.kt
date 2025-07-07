class Canceled : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        shipment.status = update.updateType
    }
}
