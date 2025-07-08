package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

fun main() {

    val trackingSimulator = TrackingSimulator("test.txt")
    runBlocking{
        trackingSimulator.runSimulation()
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Assignment_2",
        ) {
            App()
        }
    }

}

