import java.io.File
import kotlin.system.exitProcess

class D5700Computer private constructor() {
    private val cpu = CPU()
    private val memory = Memory()
    private val display = Display()
    private val keyboard = Keyboard()
    private val scheduler = CPUScheduler.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: D5700Computer? = null

        fun getInstance(): D5700Computer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: D5700Computer().also { INSTANCE = it }
            }
        }
    }

    fun loadProgram(filePath: String) {
        try {
            val romData = File(filePath).readBytes()
            if (romData.size > 4096) {
                throw IllegalArgumentException("ROM file too large (max 4KB)")
            }
            memory.loadROM(romData)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load program: ${e.message}")
        }
    }

    fun start() {
        cpu.initialize(memory, display, keyboard)

        cpu.setShutdownCallback {
            stop()
            exitProcess(0) // Exit the program cleanly
        }

        display.clear()
        display.render()
        scheduler.scheduleExecution(cpu)

        try {
            Thread.currentThread().join()
        } catch (e: InterruptedException) {
            stop()
        }
    }

    fun stop() {
        scheduler.shutdown()
    }
}