package org.example.project

import java.util.*

class Shipment(
    var id: String,
    var status: String,
    var expectedDeliveryDateTimestamp: Long,
    var currentLocation: String
): Subject<ShipmentObserver> {
    val notes = mutableListOf<String>()
    val updateHistory = mutableListOf<ShipmentUpdate>()

    private val observers = mutableListOf<ShipmentObserver>()

    fun addNote(note: String) {
        notes += note
    }

    fun addUpdate(update: ShipmentUpdate) {
        updateHistory += update
        notifyObservers()
    }

    override fun addObserver(observer: ShipmentObserver) {
        observers += observer
        observer.update(id, status, expectedDeliveryDateTimestamp, currentLocation, notes, updateHistory)
    }

    override fun removeObserver(observer: ShipmentObserver) {
        observers -= observer
    }

    override fun notifyObservers() {
        observers.forEach{it.update(id, status, expectedDeliveryDateTimestamp, currentLocation, notes, updateHistory)}
    }
}
