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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import assignment_2.composeapp.generated.resources.Res
import assignment_2.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App(trackingServer: TrackingServer) {
    var shipmentId by remember { mutableStateOf("") }
    val helpers = remember { mutableStateListOf<TrackerViewHelper>() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showError) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            showError = false
        }
    }

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
                val shipment = trackingServer.findShipment(shipmentId)
                if (shipment == null) {
                    errorMessage = "Shipment ID \"$shipmentId\" not found."
                    showError = true
                } else if (helpers.any { it.id == shipmentId }) {
                    errorMessage = "Shipment ID \"$shipmentId\" is already being tracked."
                    showError = true
                } else {
                    val newHelper = TrackerViewHelper()
                    shipment.addObserver(newHelper)
                    helpers.add(newHelper)
                    showError = false
                }
            }) {
                Text("Track Shipment")
            }

            AnimatedVisibility(visible = showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(helpers, key = { it.id }) { helper ->
                    TrackerCard(
                        helper = helper,
                        onClose = { helpers.remove(helper) }
                    )
                }
            }
        }
    }
}
