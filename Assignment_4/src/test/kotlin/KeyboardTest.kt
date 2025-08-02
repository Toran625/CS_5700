import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeyboardTest {
    private lateinit var keyboard: Keyboard
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var originalOut: PrintStream

    @BeforeEach
    fun setup() {
        keyboard = Keyboard()
        outputStream = ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(PrintStream(outputStream))
    }

    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `test valid single hex digit input`() {
        val input = "5\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0x05.toByte(), result)
    }

    @Test
    fun `test valid two hex digit input`() {
        val input = "A3\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0xA3.toByte(), result)
    }

    @Test
    fun `test lowercase hex input`() {
        val input = "ff\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0xFF.toByte(), result)
    }

    @Test
    fun `test mixed case hex input`() {
        val input = "aB\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0xAB.toByte(), result)
    }

    @Test
    fun `test empty input returns zero`() {
        val input = "\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0x00.toByte(), result)
    }

    @Test
    fun `test invalid input returns zero`() {
        val input = "xyz\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0x00.toByte(), result)
    }

    @Test
    fun `test input longer than two characters truncated`() {
        val input = "123456\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0x12.toByte(), result) // Only first two characters
    }

    @Test
    fun `test input with special characters returns zero`() {
        val input = "!@\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        val result = keyboard.waitForInput()
        
        assertEquals(0x00.toByte(), result)
    }

    @Test
    fun `test boundary hex values`() {
        // Test 00
        System.setIn(ByteArrayInputStream("00\n".toByteArray()))
        assertEquals(0x00.toByte(), keyboard.waitForInput())
        
        // Test FF
        System.setIn(ByteArrayInputStream("FF\n".toByteArray()))
        assertEquals(0xFF.toByte(), keyboard.waitForInput())
    }

    @Test
    fun `test all valid hex digits`() {
        val validHexDigits = "0123456789ABCDEF"
        
        for (digit in validHexDigits) {
            System.setIn(ByteArrayInputStream("$digit\n".toByteArray()))
            val result = keyboard.waitForInput()
            val expected = digit.toString().toInt(16).toByte()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `test prompt message is displayed`() {
        val input = "42\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        keyboard.waitForInput()
        
        val output = outputStream.toString()
        assertTrue(output.contains("Enter hex value (0-FF):"))
    }

    @Test
    fun `test invalid input shows error message`() {
        val input = "invalid\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
        
        keyboard.waitForInput()
        
        val output = outputStream.toString()
        assertTrue(output.contains("Invalid hex input, using 0"))
    }
    
    private fun validateHexInput(input: String): Byte {
        if (input.isEmpty()) return 0

        try {
            val cleanInput = input.take(2).uppercase()
            return cleanInput.toInt(16).toByte()
        } catch (e: NumberFormatException) {
            println("Invalid hex input, using 0")
            return 0
        }
    }
}