package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import assignment_2.composeapp.generated.resources.Res
import assignment_2.composeapp.generated.resources.compose_multiplatform



@Composable
@Preview
fun App(trackingSimulator: TrackingSimulator) {
    var shipmentId by remember { mutableStateOf("") }
    var helper by remember { mutableStateOf<TrackerViewHelper?>(null) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = shipmentId,
                onValueChange = { shipmentId = it },
                label = { Text("Enter Shipment ID") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val shipment = trackingSimulator.findShipment(shipmentId)
                if (shipment != null) {
                    val newHelper = TrackerViewHelper()
                    shipment.addObserver(newHelper)
                    helper = newHelper
                }
            }) {
                Text("Subscribe to Shipment")
            }

            Spacer(modifier = Modifier.height(24.dp))

            helper?.let {
                Text("Shipment ID: ${it.id}")
                Text("Status: ${it.status}")
                Text("Location: ${it.currentLocation}")
                Text("Expected Delivery: ${it.expectedDeliveryDateTimestamp}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes:")
                it.notes.forEach { note -> Text("- $note") }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Update History:")
                it.updateHistory.forEach { update ->
                    Text("- ${update.updateType} at ${update.timestamp}")
                }
            }
        }
    }
}
