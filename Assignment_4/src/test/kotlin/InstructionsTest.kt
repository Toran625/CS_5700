import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstructionsTest {
    private lateinit var cpu: CPU
    private lateinit var memory: Memory
    private lateinit var display: Display
    private lateinit var keyboard: Keyboard

    @BeforeEach
    fun setup() {
        cpu = spy(CPU())
        memory = mock<Memory>()
        display = mock<Display>()
        keyboard = mock<Keyboard>()
        cpu.initialize(memory, display, keyboard)
    }

    // Store Instruction Tests
    @Test
    fun `test Store instruction`() {
        val instruction = Store()
        val instructionWord = 0x05FF.toShort() // Store FF in register 5
        
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(5, 0xFF.toByte())
        verify(cpu).incrementProgramCounter()
    }

    // Add Instruction Tests
    @Test
    fun `test Add instruction`() {
        val instruction = Add()
        whenever(cpu.getRegister(1)).thenReturn(0x10.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x20.toByte())
        
        val instructionWord = 0x1123.toShort() // Add r1 + r2 -> r3
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(3, 0x30.toByte())
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test Add instruction with overflow`() {
        val instruction = Add()
        whenever(cpu.getRegister(1)).thenReturn(0xFF.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x01.toByte())
        
        val instructionWord = 0x1123.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(3, 0x00.toByte()) // 0xFF + 0x01 = 0x100, truncated to 0x00
    }

    // Sub Instruction Tests
    @Test
    fun `test Sub instruction`() {
        val instruction = Sub()
        whenever(cpu.getRegister(1)).thenReturn(0x30.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x10.toByte())
        
        val instructionWord = 0x2123.toShort() // r1 - r2 -> r3
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(3, 0x20.toByte())
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test Sub instruction with underflow`() {
        val instruction = Sub()
        whenever(cpu.getRegister(1)).thenReturn(0x10.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x20.toByte())
        
        val instructionWord = 0x2123.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(3, 0xF0.toByte()) // 0x10 - 0x20 = -0x10, as unsigned byte = 0xF0
    }

    // Read Instruction Tests
    @Test
    fun `test Read instruction from RAM`() {
        val instruction = Read()
        whenever(cpu.getAddress()).thenReturn(0x100.toShort())
        whenever(cpu.getMemoryFlag()).thenReturn(false)
        whenever(memory.readByte(0x100.toShort(), false)).thenReturn(0x42.toByte())
        
        val instructionWord = 0x3500.toShort() // Read to r5
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(5, 0x42.toByte())
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test Read instruction from ROM`() {
        val instruction = Read()
        whenever(cpu.getAddress()).thenReturn(0x200.toShort())
        whenever(cpu.getMemoryFlag()).thenReturn(true)
        whenever(memory.readByte(0x200.toShort(), true)).thenReturn(0x84.toByte())
        
        val instructionWord = 0x3700.toShort() // Read to r7
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(7, 0x84.toByte())
        verify(cpu).incrementProgramCounter()
    }

    // Write Instruction Tests
    @Test
    fun `test Write instruction to RAM`() {
        val instruction = Write()
        whenever(cpu.getAddress()).thenReturn(0x150.toShort())
        whenever(cpu.getMemoryFlag()).thenReturn(false)
        whenever(cpu.getRegister(3)).thenReturn(0x99.toByte())
        
        val instructionWord = 0x4300.toShort() // Write r3
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(memory).writeByte(0x150.toShort(), 0x99.toByte(), false)
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test Write instruction to ROM`() {
        val instruction = Write()
        whenever(cpu.getAddress()).thenReturn(0x250.toShort())
        whenever(cpu.getMemoryFlag()).thenReturn(true)
        whenever(cpu.getRegister(4)).thenReturn(0xAA.toByte())
        
        val instructionWord = 0x4400.toShort() // Write r4
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(memory).writeByte(0x250.toShort(), 0xAA.toByte(), true)
        verify(cpu).incrementProgramCounter()
    }

    // Jump Instruction Tests
    @Test
    fun `test Jump instruction`() {
        val instruction = Jump()
        
        val instructionWord = 0x5ABC.toShort() // Jump to 0xABC
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setProgramCounter(0xABC.toShort())
        verify(cpu, never()).incrementProgramCounter() // Jump doesn't increment PC
    }

    @Test
    fun `test Jump instruction should not increment PC`() {
        val instruction = Jump()
        assertFalse(instruction.shouldIncrementPC())
    }

    // ReadKeyboard Instruction Tests
    @Test
    fun `test ReadKeyboard instruction`() {
        val instruction = ReadKeyboard()
        whenever(keyboard.waitForInput()).thenReturn(0x42.toByte())
        
        val instructionWord = 0x6300.toShort() // Read keyboard to r3
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(3, 0x42.toByte())
        verify(cpu).incrementProgramCounter()
    }

    // SwitchMemory Instruction Tests
    @Test
    fun `test SwitchMemory instruction`() {
        val instruction = SwitchMemory()
        
        val instructionWord = 0x7000.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).toggleMemoryFlag()
        verify(cpu).incrementProgramCounter()
    }

    // SkipEqual Instruction Tests
    @Test
    fun `test SkipEqual instruction when registers are equal`() {
        val instruction = SkipEqual()
        whenever(cpu.getRegister(1)).thenReturn(0x42.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x42.toByte())
        
        val instructionWord = 0x8120.toShort() // Compare r1 and r2
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu, times(2)).incrementProgramCounter() // Once for skip, once normal
    }

    @Test
    fun `test SkipEqual instruction when registers are not equal`() {
        val instruction = SkipEqual()
        whenever(cpu.getRegister(1)).thenReturn(0x42.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x43.toByte())
        
        val instructionWord = 0x8120.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu, times(1)).incrementProgramCounter() // Only normal increment
    }

    // SkipNotEqual Instruction Tests
    @Test
    fun `test SkipNotEqual instruction when registers are not equal`() {
        val instruction = SkipNotEqual()
        whenever(cpu.getRegister(1)).thenReturn(0x42.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x43.toByte())
        
        val instructionWord = 0x9120.toShort() // Compare r1 and r2
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu, times(2)).incrementProgramCounter() // Once for skip, once normal
    }

    @Test
    fun `test SkipNotEqual instruction when registers are equal`() {
        val instruction = SkipNotEqual()
        whenever(cpu.getRegister(1)).thenReturn(0x42.toByte())
        whenever(cpu.getRegister(2)).thenReturn(0x42.toByte())
        
        val instructionWord = 0x9120.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu, times(1)).incrementProgramCounter() // Only normal increment
    }

    // SetA Instruction Tests
    @Test
    fun `test SetA instruction`() {
        val instruction = SetA()
        
        val instructionWord = 0xA123.toShort() // Set A to 0x123
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setAddress(0x123.toShort())
        verify(cpu).incrementProgramCounter()
    }

    // SetT Instruction Tests
    @Test
    fun `test SetT instruction`() {
        val instruction = SetT()
        
        val instructionWord = 0xB420.toShort() // Set T to 0x42
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setTimer(0x42.toByte())
        verify(cpu).incrementProgramCounter()
    }

    // ReadT Instruction Tests
    @Test
    fun `test ReadT instruction`() {
        val instruction = ReadT()
        whenever(cpu.getTimer()).thenReturn(0x55.toByte())
        
        val instructionWord = 0xC600.toShort() // Read T to r6
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(6, 0x55.toByte())
        verify(cpu).incrementProgramCounter()
    }

    // ConvertToBase10 Instruction Tests
    @Test
    fun `test ConvertToBase10 instruction`() {
        val instruction = ConvertToBase10()
        whenever(cpu.getRegister(2)).thenReturn(123.toByte()) // 123 decimal
        whenever(cpu.getAddress()).thenReturn(0x300.toShort())
        
        val instructionWord = 0xD200.toShort() // Convert r2
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(memory).writeByte(0x300.toShort(), 1.toByte(), false) // hundreds
        verify(memory).writeByte(0x301.toShort(), 2.toByte(), false) // tens
        verify(memory).writeByte(0x302.toShort(), 3.toByte(), false) // ones
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test ConvertToBase10 with 255`() {
        val instruction = ConvertToBase10()
        whenever(cpu.getRegister(1)).thenReturn(255.toByte())
        whenever(cpu.getAddress()).thenReturn(0x400.toShort())
        
        val instructionWord = 0xD100.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(memory).writeByte(0x400.toShort(), 2.toByte(), false) // hundreds
        verify(memory).writeByte(0x401.toShort(), 5.toByte(), false) // tens
        verify(memory).writeByte(0x402.toShort(), 5.toByte(), false) // ones
    }

    // ConvertByteToAscii Instruction Tests
    @Test
    fun `test ConvertByteToAscii with digit 0-9`() {
        val instruction = ConvertByteToAscii()
        whenever(cpu.getRegister(0)).thenReturn(5.toByte())
        
        val instructionWord = 0xE010.toShort() // Convert r0 to ASCII in r1
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(1, '5'.code.toByte())
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test ConvertByteToAscii with hex digit A-F`() {
        val instruction = ConvertByteToAscii()
        whenever(cpu.getRegister(0)).thenReturn(0xA.toByte())
        
        val instructionWord = 0xE010.toShort()
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(cpu).setRegister(1, 'A'.code.toByte())
        verify(cpu).incrementProgramCounter()
    }

    @Test
    fun `test ConvertByteToAscii with invalid value throws exception`() {
        val instruction = ConvertByteToAscii()
        whenever(cpu.getRegister(0)).thenReturn(0x10.toByte()) // Invalid (> 0xF)
        
        val instructionWord = 0xE010.toShort()
        
        assertThrows<RuntimeException> {
            instruction.execute(cpu, memory, display, keyboard, instructionWord)
        }
    }

    // Draw Instruction Tests
    @Test
    fun `test Draw instruction`() {
        val instruction = Draw()
        whenever(cpu.getRegister(1)).thenReturn('A'.code.toByte())
        
        val instructionWord = 0xF123.toShort() // Draw r1 at row r2, col r3
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        verify(display).writeToDisplayRAM('A'.code.toByte(), 2, 3)
        verify(display).render()
        verify(cpu).incrementProgramCounter()
    }

    // Base Instruction Tests
    @Test
    fun `test instruction operand parsing`() {
        val instruction = Store()
        val instructionWord = 0x1234.toShort()
        
        instruction.execute(cpu, memory, display, keyboard, instructionWord)
        
        // Store should use operand1 (2) as register and fullOperand (0x234) as value
        verify(cpu).setRegister(2, 0x34.toByte()) // Only low byte of fullOperand
    }

    @Test
    fun `test all instructions increment PC by default`() {
        val instructions = listOf(
            Store(), Add(), Sub(), Read(), Write(), ReadKeyboard(),
            SwitchMemory(), SkipEqual(), SkipNotEqual(), SetA(), SetT(),
            ReadT(), ConvertToBase10(), ConvertByteToAscii(), Draw()
        )
        
        instructions.forEach { instruction ->
            assertTrue(instruction.shouldIncrementPC(), "${instruction::class.simpleName} should increment PC")
        }
    }

    @Test
    fun `test Jump instruction does not increment PC`() {
        val jump = Jump()
        assertFalse(jump.shouldIncrementPC())
    }
}