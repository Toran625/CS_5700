import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegrationTest {
    private lateinit var cpu: CPU
    private lateinit var memory: Memory
    private lateinit var display: Display
    private lateinit var keyboard: Keyboard
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var originalOut: PrintStream

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        cpu = CPU()
        memory = Memory()
        display = Display()
        keyboard = Keyboard()
        cpu.initialize(memory, display, keyboard)
        
        outputStream = ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(PrintStream(outputStream))
    }

    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `test complete program execution - Hello World`() {
        // Program to display "HI" on screen
        val program = byteArrayOf(
            // Store 'H' (0x48) in r0 and draw at (0,0)
            0x00.toByte(), 0x48.toByte(), // STORE 0x48 in r0
            0x00.toByte(), 0x10.toByte(), // STORE 0x00 in r1 (row)
            0x00.toByte(), 0x20.toByte(), // STORE 0x00 in r2 (col)
            0xF0.toByte(), 0x12.toByte(), // DRAW r0 at row r1, col r2
            
            // Store 'I' (0x49) in r0 and draw at (0,1)
            0x00.toByte(), 0x49.toByte(), // STORE 0x49 in r0
            0x00.toByte(), 0x21.toByte(), // STORE 0x01 in r2 (col)
            0xF0.toByte(), 0x12.toByte(), // DRAW r0 at row r1, col r2
            
            // Terminate
            0x00.toByte(), 0x00.toByte()
        )
        
        memory.loadROM(program)
        
        // Execute instructions until termination
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        val output = outputStream.toString()
        assertTrue(output.contains("HI"))
    }

    @Test
    fun `test arithmetic operations`() {
        // Program to test ADD and SUB operations
        val program = byteArrayOf(
            0x00.toByte(), 0x05.toByte(), // STORE 5 in r0
            0x00.toByte(), 0x13.toByte(), // STORE 3 in r1
            0x11.toByte(), 0x02.toByte(), // ADD r0 + r1 -> r2 (should be 8)
            0x20.toByte(), 0x13.toByte(), // SUB r0 - r1 -> r3 (should be 2)
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals(8.toByte(), cpu.getRegister(2))
        assertEquals(2.toByte(), cpu.getRegister(3))
    }

    @Test
    fun `test memory operations`() {
        val program = byteArrayOf(
            0x00.toByte(), 0x42.toByte(), // STORE 0x42 in r0
            0xA1.toByte(), 0x00.toByte(), // SET_A to 0x100
            0x40.toByte(), 0x00.toByte(), // WRITE r0 to address A
            0x00.toByte(), 0x10.toByte(), // STORE 0x00 in r1
            0x31.toByte(), 0x00.toByte(), // READ from address A to r1
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        // r1 should contain the value we wrote to memory
        assertEquals(0x42.toByte(), cpu.getRegister(1))
    }

    @Test
    fun `test conditional jumps with skip instructions`() {
        val program = byteArrayOf(
            0x00.toByte(), 0x05.toByte(), // STORE 5 in r0
            0x00.toByte(), 0x15.toByte(), // STORE 5 in r1
            0x80.toByte(), 0x10.toByte(), // SKIP_EQUAL r0, r1 (should skip next instruction)
            0x00.toByte(), 0x2F.toByte(), // STORE 0xFF in r2 (should be skipped)
            0x00.toByte(), 0x20.toByte(), // STORE 0x00 in r2 (should execute)
            0x00.toByte(), 0x16.toByte(), // STORE 6 in r1
            0x90.toByte(), 0x10.toByte(), // SKIP_NOT_EQUAL r0, r1 (should skip next instruction)
            0x00.toByte(), 0x3F.toByte(), // STORE 0xFF in r3 (should be skipped)
            0x00.toByte(), 0x30.toByte(), // STORE 0x00 in r3 (should execute)
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        // Both r2 and r3 should be 0 (the skipped instructions stored 0xFF)
        assertEquals(0x00.toByte(), cpu.getRegister(2))
        assertEquals(0x00.toByte(), cpu.getRegister(3))
    }

    @Test
    fun `test timer operations`() {
        val program = byteArrayOf(
            0xB0.toByte(), 0xA0.toByte(), // SET_T to 0x0A (10)
            0xC0.toByte(), 0x00.toByte(), // READ_T to r0
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals(0x0A.toByte(), cpu.getRegister(0))
        assertEquals(0x0A.toByte(), cpu.getTimer())
        
        // Test timer decrement
        cpu.decrementTimer()
        assertEquals(0x09.toByte(), cpu.getTimer())
    }

    @Test
    fun `test memory flag switching`() {
        val program = byteArrayOf(
            0x70.toByte(), 0x00.toByte(), // SWITCH_MEMORY (toggle M flag)
            0x00.toByte(), 0x42.toByte(), // STORE 0x42 in r0
            0xA1.toByte(), 0x00.toByte(), // SET_A to 0x100
            0x40.toByte(), 0x00.toByte(), // WRITE r0 to ROM at address A
            0x70.toByte(), 0x00.toByte(), // SWITCH_MEMORY (back to RAM)
            0x31.toByte(), 0x00.toByte(), // READ from RAM at address A to r1 (should be 0)
            0x70.toByte(), 0x00.toByte(), // SWITCH_MEMORY (back to ROM)
            0x32.toByte(), 0x00.toByte(), // READ from ROM at address A to r2 (should be 0x42)
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals(0x00.toByte(), cpu.getRegister(1)) // RAM was empty
        assertEquals(0x42.toByte(), cpu.getRegister(2)) // ROM had our value
    }

    @Test
    fun `test base 10 conversion`() {
        val program = byteArrayOf(
            0x00.toByte(), 0x7B.toByte(), // STORE 123 in r0
            0xA2.toByte(), 0x00.toByte(), // SET_A to 0x200
            0xD0.toByte(), 0x00.toByte(), // CONVERT_TO_BASE_10 r0
            0x31.toByte(), 0x00.toByte(), // READ from A to r1 (hundreds)
            0xA2.toByte(), 0x01.toByte(), // SET_A to 0x201
            0x32.toByte(), 0x00.toByte(), // READ from A to r2 (tens)
            0xA2.toByte(), 0x02.toByte(), // SET_A to 0x202
            0x33.toByte(), 0x00.toByte(), // READ from A to r3 (ones)
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals(1.toByte(), cpu.getRegister(1)) // hundreds digit
        assertEquals(2.toByte(), cpu.getRegister(2)) // tens digit
        assertEquals(3.toByte(), cpu.getRegister(3)) // ones digit
    }

    @Test
    fun `test ASCII conversion`() {
        val program = byteArrayOf(
            0x00.toByte(), 0x05.toByte(), // STORE 5 in r0
            0xE0.toByte(), 0x10.toByte(), // CONVERT_BYTE_TO_ASCII r0 -> r1
            0x00.toByte(), 0x0A.toByte(), // STORE 0xA in r0
            0xE0.toByte(), 0x20.toByte(), // CONVERT_BYTE_TO_ASCII r0 -> r2
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals('5'.code.toByte(), cpu.getRegister(1))
        assertEquals('A'.code.toByte(), cpu.getRegister(2))
    }

    @Test
    fun `test jump instruction`() {
        val program = byteArrayOf(
            0x51.toByte(), 0x06.toByte(), // JUMP to 0x106 (skip next instruction)
            0x00.toByte(), 0x1F.toByte(), // STORE 0xFF in r1 (should be skipped)
            0x00.toByte(), 0x10.toByte(), // STORE 0x00 in r1 (at address 0x106)
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        // r1 should be 0 because the jump skipped the instruction that would set it to 0xFF
        assertEquals(0x00.toByte(), cpu.getRegister(1))
    }

    @Test
    fun `test keyboard input simulation`() {
        // Simulate keyboard input
        System.setIn(ByteArrayInputStream("42\n".toByteArray()))
        
        val program = byteArrayOf(
            0x60.toByte(), 0x00.toByte(), // READ_KEYBOARD to r0
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        assertEquals(0x42.toByte(), cpu.getRegister(0))
    }

    @Test
    fun `test complex program with multiple operations`() {
        // Program that:
        // 1. Takes two numbers (hardcoded as 15 and 7)
        // 2. Adds them
        // 3. Converts result to base 10
        // 4. Converts digits to ASCII
        // 5. Displays result on screen
        
        val program = byteArrayOf(
            // Load numbers
            0x00.toByte(), 0x0F.toByte(), // STORE 15 in r0
            0x00.toByte(), 0x17.toByte(), // STORE 7 in r1
            
            // Add them
            0x10.toByte(), 0x12.toByte(), // ADD r0 + r1 -> r2 (result: 22)
            
            // Convert to base 10
            0xA3.toByte(), 0x00.toByte(), // SET_A to 0x300
            0xD2.toByte(), 0x00.toByte(), // CONVERT_TO_BASE_10 r2
            
            // Read digits back
            0x33.toByte(), 0x00.toByte(), // READ tens digit to r3
            0xA3.toByte(), 0x02.toByte(), // SET_A to 0x302
            0x34.toByte(), 0x00.toByte(), // READ ones digit to r4
            
            // Convert to ASCII
            0xE3.toByte(), 0x50.toByte(), // CONVERT_BYTE_TO_ASCII r3 -> r5
            0xE4.toByte(), 0x60.toByte(), // CONVERT_BYTE_TO_ASCII r4 -> r6
            
            // Display on screen
            0x00.toByte(), 0x70.toByte(), // STORE 0 in r7 (row/col)
            0xF5.toByte(), 0x77.toByte(), // DRAW r5 at (0,0) - tens digit
            0x00.toByte(), 0x71.toByte(), // STORE 1 in r7 (col)
            0xF6.toByte(), 0x77.toByte(), // DRAW r6 at (0,1) - ones digit
            
            0x00.toByte(), 0x00.toByte()  // Terminate
        )
        
        memory.loadROM(program)
        
        while (cpu.isRunning()) {
            cpu.executeInstruction()
        }
        
        // Check that addition worked
        assertEquals(22.toByte(), cpu.getRegister(2))
        
        // Check ASCII conversion
        assertEquals('2'.code.toByte(), cpu.getRegister(5)) // tens digit
        assertEquals('2'.code.toByte(), cpu.getRegister(6)) // ones digit
        
        // Check that display shows "22"
        val output = outputStream.toString()
        assertTrue(output.contains("22"))
    }

    @Test
    fun `test error handling - invalid jump address`() {
        val program = byteArrayOf(
            0x51.toByte(), 0x01.toByte(), // JUMP to 0x101 (odd address - should fail)
            0x00.toByte(), 0x00.toByte()
        )
        
        memory.loadROM(program)
        
        cpu.executeInstruction() // Should handle error and stop
        
        // CPU should have stopped due to error
        assertTrue(!cpu.isRunning())
    }

    @Test
    fun `test program counter out of bounds`() {
        val program = byteArrayOf(
            0x5F.toByte(), 0xFE.toByte(), // JUMP to 0xFFE (out of bounds)
            0x00.toByte(), 0x00.toByte()
        )
        
        memory.loadROM(program)
        
        cpu.executeInstruction() // Jump instruction
        cpu.executeInstruction() // Should fail on out of bounds access
        
        // CPU should have stopped due to error
        assertTrue(!cpu.isRunning())
    }
}