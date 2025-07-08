package org.example.project

import java.util.*

class Shipment(
    id: String,
    status: String,
    expectedDeliveryDateTimestamp: Long,
    currentLocation: String
) {
    private var _id = id
    private var _status = status
    private var _expectedDeliveryDateTimestamp = expectedDeliveryDateTimestamp
    private var _currentLocation = currentLocation

    var id: String
        get() = _id
        set(value) { _id = value }

    var status: String
        get() = _status
        set(value) { _status = value }

    var expectedDeliveryDateTimestamp: Long
        get() = _expectedDeliveryDateTimestamp
        set(value) { _expectedDeliveryDateTimestamp = value }

    var currentLocation: String
        get() = _currentLocation
        set(value) { _currentLocation = value }

    val notes = mutableListOf<String>()
    val updateHistory = mutableListOf<ShipmentUpdate>()

    private val observers = mutableListOf<TrackerViewHelper>()

    fun addNote(note: String) {
        notes.add(note)
        // notifyObservers()
    }

    fun addUpdate(update: ShipmentUpdate) {
        println("Update added $update")
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
