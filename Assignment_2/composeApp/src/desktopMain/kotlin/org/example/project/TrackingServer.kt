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

                    val (updateType, shipmentId, timestamp, otherInfo, method) = parsedUpdate

                    val shipment = if (updateType.lowercase() == "created" && !shipments.containsKey(shipmentId)) {
                        addShipment(shipmentId, updateType, otherInfo)
                    } else {
                        findShipment(shipmentId)
                    }

                    if (shipment == null) {
                        call.respond(HttpStatusCode.NotFound, "Shipment not found.")
                        return@post
                    }

                    shipment.receiveUpdate(updateType, shipmentId, timestamp, otherInfo, method)

                    call.respond(HttpStatusCode.OK, "Update applied.")
                }
                staticResources("/", "static") {
                    default("index.html")
                }

            }
        }.start(wait = false)
    }


    fun findShipment(id: String): Shipment? = shipments[id]


//    fun addShipment(id: String, initialStatus: String, otherInfo: String?): Shipment {
//        val shipment = Shipment(
//            id = id,
//            status = initialStatus,
//            expectedDeliveryDateTimestamp = 0,
//            currentLocation = otherInfo ?: "Origin Facility"
//        )
//        shipments[id] = shipment
//        return shipment
//    }

    fun addShipment(id: String, initialStatus: String, otherInfo: String?): Shipment {
        val expectedDelivery = 0L
        val location = "Origin Facility"

        val shipment: Shipment = when (otherInfo?.lowercase()?.trim()) {
            "bulk" -> BulkShipment(id, initialStatus, expectedDelivery, location)
            "express" -> ExpressShipment(id, initialStatus, expectedDelivery, location)
            "overnight" -> OvernightShipment(id, initialStatus, expectedDelivery, location)
            else -> StandardShipment(id, initialStatus, expectedDelivery, location)
        }

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

        val timestamp = timestampStr.toLongOrNull() ?: return null

        val method = getUpdateMethodFor(updateType)

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

private data class ParsedUpdate(
    val updateType: String,
    val shipmentId: String,
    val timestamp: Long,
    val otherInfo: String?,
    val method: UpdateMethod
)
