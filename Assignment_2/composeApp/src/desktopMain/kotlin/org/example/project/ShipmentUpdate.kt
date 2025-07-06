data class ShipmentUpdate(
    val previousStatus: String,
    val newStatus: String,
    val timestamp: Long,
    var method: UpdateMethod,
    var otherInfo: Any? = null
){
    fun applyToShipment(shipment: Shipment){
        method.processInfo(this, shipment)
    }
}