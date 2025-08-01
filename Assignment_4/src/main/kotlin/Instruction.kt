abstract class Instruction {
    protected var operand1: Int = 0
    protected var operand2: Int = 0
    protected var operand3: Int = 0
    protected var fullOperand: Int = 0

    fun execute(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard, instructionWord: Short) {
        parseOperands(instructionWord)
        performOperation(cpu, memory, display, keyboard)
        if (shouldIncrementPC()) {
            cpu.incrementProgramCounter()
        }
    }

    private fun parseOperands(instructionWord: Short) {
        val word = instructionWord.toInt() and 0xFFFF
        operand1 = (word and 0x0F00) ushr 8
        operand2 = (word and 0x00F0) ushr 4
        operand3 = word and 0x000F
        fullOperand = word and 0x0FFF
    }

    abstract fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard)
    open fun shouldIncrementPC(): Boolean = true
}