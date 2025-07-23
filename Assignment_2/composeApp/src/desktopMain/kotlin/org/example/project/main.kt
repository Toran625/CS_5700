package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

fun main() {

    val trackingServer = TrackingServer()

    application {
        CoroutineScope(Dispatchers.Default).launch{
            trackingServer.startServer()
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "Assignment_2",
        ) {
            App(trackingServer)
        }
    }
}

