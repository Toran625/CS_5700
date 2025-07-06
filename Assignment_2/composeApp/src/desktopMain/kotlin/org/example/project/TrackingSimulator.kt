package org.example.project
import kotlinx.coroutines.*
import java.io.*

class TrackingSimulator (private val filePath: String){
    private val scope = CoroutineScope(Dispatchers.Default)
    private val shipments = mutableMapOf<String, Shipment>()
    private var job: Job? = null

    fun runSimulation(){
        if (job != null) return

        job = scope.launch {
            val file = File(filePath)
            if(!file.exists()){
                println("Update file not found at $filePath")
                return@launch
            }

            BufferedReader(FileReader(file)).useLines {lines ->
                for (line in lines){
                    if(line.isBlank()) continue

                    val parts = line.split(",")
                    if (parts.size < 3 || parts.size > 4) continue

                    val status = parts[0].trim()
                    val shipmentId = parts[1].trim()
                    val timestamp = parts[2].trim()

                }
            }
        }
    }
    fun start(){
        scope.launch{
            println("Backend service start on thread: ${Thread.currentThread().name}")
            while(true){
                println("Backend is working...")
                delay(3000)
            }
        }
    }
}