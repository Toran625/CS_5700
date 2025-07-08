package org.example.project

interface ShipmentObserver {
    fun update(id: String, status: String, expectedDeliveryDateTimestamp: Long, currentLocation: String)
}