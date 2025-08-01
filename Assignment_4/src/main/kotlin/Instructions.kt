class Store : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        val value = (fullOperand and 0xFF).toByte()
        cpu.setRegister(register, value)
    }
}

class Add : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val regY = operand2
        val regZ = operand3

        val result = (cpu.getRegister(regX).toInt() and 0xFF) + (cpu.getRegister(regY).toInt() and 0xFF)
        cpu.setRegister(regZ, (result and 0xFF).toByte())
    }
}

class Sub : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val regY = operand2
        val regZ = operand3

        val result = (cpu.getRegister(regX).toInt() and 0xFF) - (cpu.getRegister(regY).toInt() and 0xFF)
        cpu.setRegister(regZ, (result and 0xFF).toByte())
    }
}

class Read : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        val value = memory.readByte(cpu.getAddress(), cpu.getMemoryFlag())
        cpu.setRegister(register, value)
    }
}

class Write : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        val value = cpu.getRegister(register)
        memory.writeByte(cpu.getAddress(), value, cpu.getMemoryFlag())
    }
}

class Jump : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val address = fullOperand.toShort()
        cpu.setProgramCounter(address)
    }

    override fun shouldIncrementPC(): Boolean = false
}

class ReadKeyboard : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        val input = keyboard.waitForInput()
        cpu.setRegister(register, input)
    }
}

class SwitchMemory : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        cpu.toggleMemoryFlag()
    }
}

class SkipEqual : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val regY = operand2

        if (cpu.getRegister(regX) == cpu.getRegister(regY)) {
            cpu.incrementProgramCounter()
        }
    }
}

class SkipNotEqual : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val regY = operand2

        if (cpu.getRegister(regX) != cpu.getRegister(regY)) {
            cpu.incrementProgramCounter()
        }
    }
}

class SetA : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val address = fullOperand.toShort()
        cpu.setAddress(address)
    }
}

class SetT : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val value = (fullOperand ushr 4).toByte()
        cpu.setTimer(value)
    }
}

class ReadT : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        cpu.setRegister(register, cpu.getTimer())
    }
}

class ConvertToBase10 : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val register = operand1
        val value = cpu.getRegister(register).toInt() and 0xFF
        val address = cpu.getAddress()

        val hundreds = (value / 100).toByte()
        val tens = ((value % 100) / 10).toByte()
        val ones = (value % 10).toByte()

        memory.writeByte(address, hundreds, false)
        memory.writeByte((address + 1).toShort(), tens, false)
        memory.writeByte((address + 2).toShort(), ones, false)
    }
}

class ConvertByteToAscii : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val regY = operand2
        val digit = cpu.getRegister(regX).toInt() and 0xFF

        if (digit > 0xF) {
            throw RuntimeException("Digit value too large for ASCII conversion: $digit")
        }

        val ascii = if (digit <= 9) {
            (digit + '0'.code).toByte()
        } else {
            (digit - 10 + 'A'.code).toByte()
        }

        cpu.setRegister(regY, ascii)
    }
}

class Draw : Instruction() {
    override fun performOperation(cpu: CPU, memory: Memory, display: Display, keyboard: Keyboard) {
        val regX = operand1
        val row = operand2
        val col = operand3

        val asciiValue = cpu.getRegister(regX)
        display.writeToDisplayRAM(asciiValue, row, col)
        display.render()
    }
}