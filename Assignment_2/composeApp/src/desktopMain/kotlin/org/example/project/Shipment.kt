import java.util.*

class Shipment(
    val id: String,
    var status: String,
    val expectedDeliveryDateTimestamp: Long,
    var currentLocation: String
) {
    val notes = mutableListOf<String>()
    val updateHistory = mutableListOf<ShipmentUpdate>()
    private val observers = mutableListOf<TrackerViewHelper>()

    fun addNote(note: String) {
        notes.add(note)
        // notifyObservers()
    }

    fun addUpdate(update: ShipmentUpdate) {
        status = update.newStatus
        updateHistory.add(update)
        // notifyObservers()
    }

    fun addObserver(observer: TrackerViewHelper) {
        observers.add(observer)
    }

    fun removeObserver(observer: TrackerViewHelper) {
        observers.remove(observer)
    }

    // fun notifyObservers() {
    //     observers.forEach { it.onShipmentUpdated(this) }
    // }
}
