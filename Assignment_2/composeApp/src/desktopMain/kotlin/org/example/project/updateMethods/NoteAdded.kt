class NoteAdded : UpdateMethod {
    override fun processInfo(update: ShipmentUpdate, shipment: Shipment) {
        update.otherInfo?.let {
            shipment.addNote(it)
        }
    }
}
