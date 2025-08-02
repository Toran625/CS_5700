class CPU {
    private val registers = ByteArray(8) // r0-r7
    private var programCounter: Short = 0
    private var timer: Byte = 0
    private var address: Short = 0
    private var memoryFlag: Boolean = false // 0=RAM, 1=ROM
    private var running: Boolean = true

    private lateinit var memory: Memory
    private lateinit var display: Display
    private lateinit var keyboard: Keyboard
    private val instructionFactory = InstructionFactory()

    fun initialize(memory: Memory, display: Display, keyboard: Keyboard) {
        this.memory = memory
        this.display = display
        this.keyboard = keyboard
    }

    fun executeInstruction() {
        if (!running) return

        try {
            val instructionWord = fetchInstruction()
            if (instructionWord.toInt() == 0x0000) {
                println("Program terminated normally (0000 instruction)")
                running = false
                return
            }

            val opcode = ((instructionWord.toInt() and 0xF000) ushr 12).toByte()
            val instruction = instructionFactory.createInstruction(opcode)
            instruction.execute(this, memory, display, keyboard, instructionWord)

        } catch (e: Exception) {
            println("CPU Error: ${e.message}")
            running = false
        }
    }

    private fun fetchInstruction(): Short {
        if (programCounter < 0 || programCounter >= 4096) {
            throw RuntimeException("Program counter out of bounds: $programCounter")
        }

        val byte1 = memory.readByte(programCounter, true).toInt() and 0xFF
        val byte2 = memory.readByte((programCounter + 1).toShort(), true).toInt() and 0xFF
        return ((byte1 shl 8) or byte2).toShort()
    }

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

    fun getRegister(index: Int): Byte = registers[index]
    fun setRegister(index: Int, value: Byte) { registers[index] = value }

    fun getProgramCounter(): Short = programCounter
    fun setProgramCounter(value: Short) {
        if (value % 2 != 0) {
            throw RuntimeException("Program counter must be even: $value")
        }
        programCounter = value
    }
    fun incrementProgramCounter() { programCounter = (programCounter + 2).toShort() }

    fun getTimer(): Byte = timer
    fun setTimer(value: Byte) { timer = value }

    fun getAddress(): Short = address
    fun setAddress(value: Short) { address = value }

    fun getMemoryFlag(): Boolean = memoryFlag
    fun setMemoryFlag(value: Boolean) { memoryFlag = value }
    fun toggleMemoryFlag() { memoryFlag = !memoryFlag }
}