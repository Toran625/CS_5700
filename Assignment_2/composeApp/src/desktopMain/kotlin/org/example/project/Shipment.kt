package org.example.project

import java.util.*

class Shipment(
    var id: String,
    var status: String,
    var expectedDeliveryDateTimestamp: Long,
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
        updateHistory.add(update)
        notifyObservers()
    }

    fun addObserver(observer: TrackerViewHelper) {
        observers.add(observer)
    }

    fun removeObserver(observer: TrackerViewHelper) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        if (id == "s10000") {
            println("id: $id")
            println("status: $status")
            println("expected delivery: $expectedDeliveryDateTimestamp")
            println("location: $currentLocation")
            notes.forEach { println(it) }
            println("\n\n\n\n\n\n\n\n\n")
        }
    }
}
