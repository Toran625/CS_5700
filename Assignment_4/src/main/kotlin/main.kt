import kotlin.system.exitProcess

fun main() {
    val emulator = D5700Computer.getInstance()

    print("Enter path to D5700 ROM file: ")
    val romPath = readLine() ?: ""

    try {
        emulator.loadProgram(romPath)
        emulator.start()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        exitProcess(1)
    }
}