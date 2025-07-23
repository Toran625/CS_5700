package org.example.project

import kotlinx.serialization.Serializable

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.staticResources


class TrackingServer() {
    private val shipments = mutableMapOf<String, Shipment>()


    fun startServer() {
        embeddedServer(Netty, port = 3000) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                post("/update") {
                    val req = call.receive<UpdateRequest>()
                    val parsedUpdate = parseUpdate(req.rawUpdate) ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Invalid update format.")
                        return@post
                    }
                    println("I got an update")
                    val (updateType, shipmentId, timestamp, otherInfo, method) = parsedUpdate

                    val shipment = if (updateType.lowercase() == "created" && !shipments.containsKey(shipmentId)) {
                        println("I made a nerd!")
                        addShipment(shipmentId, updateType, otherInfo)
                    } else {
                        println("I found a previous nerd")
                        findShipment(shipmentId)
                    }
                    println("Did i make a shipment?")
                    if (shipment == null) {
                        call.respond(HttpStatusCode.NotFound, "Shipment not found.")
                        return@post
                    }

                    shipment.receiveUpdate(updateType, shipmentId, timestamp, otherInfo, method)


                    call.respond(HttpStatusCode.OK, "Update applied.")
                }
                staticResources("/", "static") { // Maps requests to / to the 'static' resource package
                    default("index.html") // Serves index.html when a directory is requested
                }

            }
        }.start(wait = false)
    }


    fun findShipment(id: String): Shipment? = shipments[id]


    fun addShipment(id: String, initialStatus: String, otherInfo: String?): Shipment {
        val shipment = Shipment(
            id = id,
            status = initialStatus,
            expectedDeliveryDateTimestamp = 0,
            currentLocation = otherInfo ?: "Origin Facility"
        )
        shipments[id] = shipment
        return shipment
    }


    private fun parseUpdate(line: String): ParsedUpdate? {
        val parts = line.split(",", limit = 4)
        if (parts.size < 3) return null

        val updateType = parts[0]
        val shipmentId = parts[1]
        val timestampStr = parts[2]
        val otherInfo = parts.getOrNull(3)

        // Validate timestamp
        val timestamp = timestampStr.toLongOrNull() ?: return null

        // Get method (throws if unknown)
        val method = getUpdateMethodFor(updateType)

        // You can add more logic here if needed, like checking shipment existence, etc.

        return ParsedUpdate(updateType, shipmentId, timestamp, otherInfo, method)
    }



    private fun getUpdateMethodFor(status: String): UpdateMethod {
        return when (status.lowercase()) {
            "created" -> Created()
            "shipped" -> Shipped()
            "location" -> Location()
            "delivered" -> Delivered()
            "delayed" -> Delayed()
            "lost" -> Lost()
            "canceled" -> Canceled()
            "noteadded" -> NoteAdded()
            else -> throw IllegalArgumentException("Unknown update method: $status")
        }
    }
}

@Serializable
data class UpdateRequest(val rawUpdate: String)

data class ParsedUpdate(
    val updateType: String,
    val shipmentId: String,
    val timestamp: Long,
    val otherInfo: String?,
    val method: UpdateMethod
)
