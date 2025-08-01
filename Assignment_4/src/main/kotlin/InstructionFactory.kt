class InstructionFactory {
    private val instructionMap = mapOf<Byte, () -> Instruction>(
        0x0.toByte() to { Store() },
        0x1.toByte() to { Add() },
        0x2.toByte() to { Sub() },
        0x3.toByte() to { Read() },
        0x4.toByte() to { Write() },
        0x5.toByte() to { Jump() },
        0x6.toByte() to { ReadKeyboard() },
        0x7.toByte() to { SwitchMemory() },
        0x8.toByte() to { SkipEqual() },
        0x9.toByte() to { SkipNotEqual() },
        0xA.toByte() to { SetA() },
        0xB.toByte() to { SetT() },
        0xC.toByte() to { ReadT() },
        0xD.toByte() to { ConvertToBase10() },
        0xE.toByte() to { ConvertByteToAscii() },
        0xF.toByte() to { Draw() }
    )

    fun createInstruction(opcode: Byte): Instruction {
        val factory = instructionMap[opcode]
            ?: throw RuntimeException("Unknown instruction opcode: ${opcode.toString(16)}")
        return factory()
    }
}