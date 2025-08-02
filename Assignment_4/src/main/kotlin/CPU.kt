class CPU {
    private val registers = ByteArray(8) // r0-r7
    private var programCounter: Short = 0
    private var timer: Byte = 0
    private var address: Short = 0
    private var memoryFlag: Boolean = false // 0=RAM, 1=ROM
    private var running: Boolean = true

//    private lateinit var memory: Memory
//    private lateinit var display: Display
//    private lateinit var keyboard: Keyboard

//    fun initialize(memory: Memory, display: Display, keyboard: Keyboard) {
//        this.memory = memory
//        this.display = display
//        this.keyboard = keyboard
//    }

    fun executeInstruction() {
        if (!running) return

        //work on this next
    }

//    private fun fetchInstruction(): Short {
//        fetch instruction from memory
//    }

    fun decrementTimer() {
        if (timer > 0) {
            timer = (timer - 1).toByte()
        }
    }

    fun isRunning(): Boolean = running

    fun reset() {
        registers.fill(0)
        programCounter = 0
        timer = 0
        address = 0
        memoryFlag = false
        running = true
    }


}