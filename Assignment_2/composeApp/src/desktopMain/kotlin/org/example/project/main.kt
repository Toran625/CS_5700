package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {

    val trackingSimulator = TrackingSimulator()
    trackingSimulator.start()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Assignment_2",
        ) {
            App()
        }
    }

}

