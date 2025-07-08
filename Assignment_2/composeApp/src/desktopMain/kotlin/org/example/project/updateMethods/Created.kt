package org.example.project

class Created : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        shipment.status = update.updateType
    }
}
