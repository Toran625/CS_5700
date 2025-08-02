import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class InstructionFactoryTest {
    private lateinit var factory: InstructionFactory

    @BeforeEach
    fun setup() {
        factory = InstructionFactory()
    }

    @Test
    fun `test create Store instruction`() {
        val instruction = factory.createInstruction(0x0.toByte())
        assertTrue(instruction is Store)
    }

    @Test
    fun `test create Add instruction`() {
        val instruction = factory.createInstruction(0x1.toByte())
        assertTrue(instruction is Add)
    }

    @Test
    fun `test create Sub instruction`() {
        val instruction = factory.createInstruction(0x2.toByte())
        assertTrue(instruction is Sub)
    }

    @Test
    fun `test create Read instruction`() {
        val instruction = factory.createInstruction(0x3.toByte())
        assertTrue(instruction is Read)
    }

    @Test
    fun `test create Write instruction`() {
        val instruction = factory.createInstruction(0x4.toByte())
        assertTrue(instruction is Write)
    }

    @Test
    fun `test create Jump instruction`() {
        val instruction = factory.createInstruction(0x5.toByte())
        assertTrue(instruction is Jump)
    }

    @Test
    fun `test create ReadKeyboard instruction`() {
        val instruction = factory.createInstruction(0x6.toByte())
        assertTrue(instruction is ReadKeyboard)
    }

    @Test
    fun `test create SwitchMemory instruction`() {
        val instruction = factory.createInstruction(0x7.toByte())
        assertTrue(instruction is SwitchMemory)
    }

    @Test
    fun `test create SkipEqual instruction`() {
        val instruction = factory.createInstruction(0x8.toByte())
        assertTrue(instruction is SkipEqual)
    }

    @Test
    fun `test create SkipNotEqual instruction`() {
        val instruction = factory.createInstruction(0x9.toByte())
        assertTrue(instruction is SkipNotEqual)
    }

    @Test
    fun `test create SetA instruction`() {
        val instruction = factory.createInstruction(0xA.toByte())
        assertTrue(instruction is SetA)
    }

    @Test
    fun `test create SetT instruction`() {
        val instruction = factory.createInstruction(0xB.toByte())
        assertTrue(instruction is SetT)
    }

    @Test
    fun `test create ReadT instruction`() {
        val instruction = factory.createInstruction(0xC.toByte())
        assertTrue(instruction is ReadT)
    }

    @Test
    fun `test create ConvertToBase10 instruction`() {
        val instruction = factory.createInstruction(0xD.toByte())
        assertTrue(instruction is ConvertToBase10)
    }

    @Test
    fun `test create ConvertByteToAscii instruction`() {
        val instruction = factory.createInstruction(0xE.toByte())
        assertTrue(instruction is ConvertByteToAscii)
    }

    @Test
    fun `test create Draw instruction`() {
        val instruction = factory.createInstruction(0xF.toByte())
        assertTrue(instruction is Draw)
    }

    @Test
    fun `test unknown opcode throws exception`() {
        val exception = assertThrows<RuntimeException> {
            factory.createInstruction(0x10.toByte()) // Invalid opcode
        }
        
        assertTrue(exception.message!!.contains("Unknown instruction opcode"))
    }

    @Test
    fun `test negative opcode throws exception`() {
        val exception = assertThrows<RuntimeException> {
            factory.createInstruction((-1).toByte())
        }
        
        assertTrue(exception.message!!.contains("Unknown instruction opcode"))
    }

    @Test
    fun `test all valid opcodes create different instruction types`() {
        val instructions = mutableSetOf<String>()
        
        for (opcode in 0x0..0xF) {
            val instruction = factory.createInstruction(opcode.toByte())
            instructions.add(instruction::class.simpleName!!)
        }
        
        // Should have 16 different instruction types
        assertEquals(16, instructions.size)
    }
}