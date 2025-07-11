package org.example.project

import androidx.compose.runtime.*

class TrackerViewHelper : ShipmentObserver{

    var id by mutableStateOf("")
    var status by mutableStateOf("")
    var expectedDeliveryDateTimestamp by mutableStateOf(0L)
    var currentLocation by mutableStateOf("")
    var notes = mutableStateListOf<String>()
    var updateHistory = mutableStateListOf<ShipmentUpdate>()

    override fun update(
        id: String,
        status: String,
        expectedDeliveryDateTimestamp: Long,
        currentLocation: String,
        notes: MutableList<String>,
        updateHistory: MutableList<ShipmentUpdate>
    ) {
        this.id = id
        this.status = status
        this.expectedDeliveryDateTimestamp = expectedDeliveryDateTimestamp
        this.currentLocation = currentLocation

        this.notes.clear()
        this.notes.addAll(notes)

        this.updateHistory.clear()
        this.updateHistory.addAll(updateHistory)
    }
}