package org.example.project

import java.util.*

abstract class Shipment(
    var id: String,
    var status: String,
    var expectedDeliveryDateTimestamp: Long,
    var currentLocation: String,
    var createdTimestamp: Long
): Subject<ShipmentObserver> {
    abstract val shipmentType: String;
    val notes = mutableListOf<String>()
    val updateHistory = mutableListOf<ShipmentUpdate>()

    private val observers = mutableListOf<ShipmentObserver>()

    fun addNote(note: String) {
        notes += note
        notifyObservers()
    }

    abstract fun receiveUpdate(
        updateType: String,
        shipmentId: String,
        timestamp: Long,
        otherInfo: String?,
        method: UpdateMethod
    )

    fun addUpdate(update: ShipmentUpdate) {
        updateHistory += update
        notifyObservers()
    }

    override fun addObserver(observer: ShipmentObserver) {
        observers += observer
        observer.update(id, status, expectedDeliveryDateTimestamp, currentLocation, notes, updateHistory, shipmentType, createdTimestamp)
    }

    override fun removeObserver(observer: ShipmentObserver) {
        observers -= observer
    }

    override fun notifyObservers() {
        observers.forEach{it.update(id, status, expectedDeliveryDateTimestamp, currentLocation, notes, updateHistory, shipmentType, createdTimestamp)}
    }
}
