package org.example.project

class Lost : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        shipment.status = update.updateType
    }
}
