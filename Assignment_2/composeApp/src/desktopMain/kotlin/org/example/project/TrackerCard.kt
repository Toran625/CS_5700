package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties

@Composable
fun TrackerCard(
    helper: TrackerViewHelper,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Shipment ID: ${helper.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = onClose,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("✕", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Status: ${helper.status}")
                Text("Location: ${helper.currentLocation}")
                val expected = helper.expectedDeliveryDateTimestamp
                Text("Expected Delivery: ${if (expected > 0) formatTimestamp(expected) else "N/A"}")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Update History:")
                helper.updateHistory.forEach { update ->
                    val time = formatTimestamp(update.timestamp)
                    Text("- Package went from \"${update.previousStatus}\" to \"${update.updateType}\" at $time")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Notes:")
                helper.notes.forEach { note ->
                    Text("- $note")
                }
            }
        }
    }
}
