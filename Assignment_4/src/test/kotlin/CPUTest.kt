import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CPUTest {
    private lateinit var cpu: CPU
    private lateinit var memory: Memory
    private lateinit var display: Display
    private lateinit var keyboard: Keyboard

    @BeforeEach
    fun setup() {
        cpu = CPU()
        memory = mock<Memory>()
        display = mock<Display>()
        keyboard = mock<Keyboard>()
        cpu.initialize(memory, display, keyboard)
    }

    @Test
    fun `test initial state`() {
        assertEquals(0, cpu.getProgramCounter())
        assertEquals(0, cpu.getTimer())
        assertEquals(0, cpu.getAddress())
        assertFalse(cpu.getMemoryFlag())
        assertTrue(cpu.isRunning())

        for (i in 0..7) {
            assertEquals(0, cpu.getRegister(i))
        }
    }

    @Test
    fun `test register operations`() {
        cpu.setRegister(0, 0x42.toByte())
        assertEquals(0x42.toByte(), cpu.getRegister(0))

        cpu.setRegister(7, 0xFF.toByte())
        assertEquals(0xFF.toByte(), cpu.getRegister(7))
    }

    @Test
    fun `test program counter operations`() {
        cpu.setProgramCounter(0x100.toShort())
        assertEquals(0x100.toShort(), cpu.getProgramCounter())

        cpu.incrementProgramCounter()
        assertEquals(0x102.toShort(), cpu.getProgramCounter())
    }

    @Test
    fun `test program counter must be even`() {
        assertThrows<RuntimeException> {
            cpu.setProgramCounter(0x101.toShort())
        }
    }

    @Test
    fun `test timer operations`() {
        cpu.setTimer(0x42.toByte())
        assertEquals(0x42.toByte(), cpu.getTimer())

        cpu.decrementTimer()
        assertEquals(0x41.toByte(), cpu.getTimer())

        cpu.setTimer(0.toByte())
        cpu.decrementTimer()
        assertEquals(0.toByte(), cpu.getTimer()) // Should not go negative
    }

    @Test
    fun `test address operations`() {
        cpu.setAddress(0x1000.toShort())
        assertEquals(0x1000.toShort(), cpu.getAddress())
    }

    @Test
    fun `test memory flag operations`() {
        assertFalse(cpu.getMemoryFlag())

        cpu.setMemoryFlag(true)
        assertTrue(cpu.getMemoryFlag())

        cpu.toggleMemoryFlag()
        assertFalse(cpu.getMemoryFlag())

        cpu.toggleMemoryFlag()
        assertTrue(cpu.getMemoryFlag())
    }

    @Test
    fun `test reset`() {
        cpu.setRegister(0, 0x42.toByte())
        cpu.setProgramCounter(0x100.toShort())
        cpu.setTimer(0x10.toByte())
        cpu.setAddress(0x200.toShort())
        cpu.setMemoryFlag(true)

        cpu.reset()

        assertEquals(0, cpu.getProgramCounter())
        assertEquals(0, cpu.getTimer())
        assertEquals(0, cpu.getAddress())
        assertFalse(cpu.getMemoryFlag())
        assertTrue(cpu.isRunning())
        assertEquals(0, cpu.getRegister(0))
    }

    @Test
    fun `test execute instruction with 0000 terminates program`() {
        whenever(memory.readByte(eq(0.toShort()), eq(true))).thenReturn(0x00.toByte())
        whenever(memory.readByte(eq(1.toShort()), eq(true))).thenReturn(0x00.toByte())

        cpu.executeInstruction()

        assertFalse(cpu.isRunning())
    }

    @Test
    fun `test execute instruction with program counter out of bounds`() {
        cpu.setProgramCounter(4096.toShort())

        cpu.executeInstruction()

        assertFalse(cpu.isRunning())
    }

    @Test
    fun `test execute instruction when not running`() {
        cpu.reset()
        whenever(memory.readByte(eq(0.toShort()), eq(true))).thenReturn(0x00.toByte())
        whenever(memory.readByte(eq(1.toShort()), eq(true))).thenReturn(0x00.toByte())

        cpu.executeInstruction() // This will stop the CPU (reads 2 bytes)

        // Verify CPU is stopped
        assertFalse(cpu.isRunning())

        // Reset the mock to clear previous interactions
        reset(memory)

        cpu.executeInstruction() // This should do nothing since CPU is stopped

        // Verify that no memory reads occurred after CPU was stopped
        verify(memory, never()).readByte(any(), any())
    }
}