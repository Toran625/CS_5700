class Location : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        update.otherInfo?.let {
            shipment.currentLocation = it
        }
    }
}
