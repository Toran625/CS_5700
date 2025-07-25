package org.example.project

import androidx.compose.runtime.*

class TrackerViewHelper : ShipmentObserver{

    var id by mutableStateOf("")
    var status by mutableStateOf("")
    var expectedDeliveryDateTimestamp by mutableStateOf(0L)
    var currentLocation by mutableStateOf("")
    var notes = mutableStateListOf<String>()
    var updateHistory = mutableStateListOf<ShipmentUpdate>()
    var shipmentType by mutableStateOf("")
    var createdTimestamp by mutableStateOf(0L)

    override fun update(
        id: String,
        status: String,
        expectedDeliveryDateTimestamp: Long,
        currentLocation: String,
        notes: MutableList<String>,
        updateHistory: MutableList<ShipmentUpdate>,
        shipmentType: String,
        createdTimestamp: Long
    ) {
        this.id = id
        this.status = status
        this.expectedDeliveryDateTimestamp = expectedDeliveryDateTimestamp
        this.currentLocation = currentLocation
        this.shipmentType = shipmentType
        this.createdTimestamp = createdTimestamp

        this.notes.clear()
        this.notes.addAll(notes)

        this.updateHistory.clear()
        this.updateHistory.addAll(updateHistory)
    }
}