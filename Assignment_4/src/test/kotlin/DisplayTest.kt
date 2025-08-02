import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class DisplayTest {
    private lateinit var display: Display
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var originalOut: PrintStream

    @BeforeEach
    fun setup() {
        display = Display()
        outputStream = ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(PrintStream(outputStream))
    }

    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `test write to display RAM within bounds`() {
        display.writeToDisplayRAM(65.toByte(), 2, 3) // ASCII 'A'

        // Should not throw exception
        display.render()

        val output = outputStream.toString()
        assertTrue(output.contains("A"))
    }

    @Test
    fun `test write to display RAM with invalid row`() {
        assertThrows<RuntimeException> {
            display.writeToDisplayRAM(65.toByte(), -1, 0)
        }

        assertThrows<RuntimeException> {
            display.writeToDisplayRAM(65.toByte(), 8, 0)
        }
    }

    @Test
    fun `test write to display RAM with invalid column`() {
        assertThrows<RuntimeException> {
            display.writeToDisplayRAM(65.toByte(), 0, -1)
        }

        assertThrows<RuntimeException> {
            display.writeToDisplayRAM(65.toByte(), 0, 8)
        }
    }

    @Test
    fun `test write to display RAM with invalid ASCII value`() {
        assertThrows<RuntimeException> {
            display.writeToDisplayRAM((-1).toByte(), 0, 0)
        }

        assertThrows<RuntimeException> {
            display.writeToDisplayRAM(128.toByte(), 0, 0)
        }
    }

    @Test
    fun `test render empty display`() {
        display.render()

        val output = outputStream.toString()
        assertTrue(output.contains("=".repeat(8)))
        assertTrue(output.contains("▫".repeat(8)))
    }

    @Test
    fun `test render display with characters`() {
        display.writeToDisplayRAM(72.toByte(), 0, 0) // 'H'
        display.writeToDisplayRAM(101.toByte(), 0, 1) // 'e'
        display.writeToDisplayRAM(108.toByte(), 0, 2) // 'l'
        display.writeToDisplayRAM(108.toByte(), 0, 3) // 'l'
        display.writeToDisplayRAM(111.toByte(), 0, 4) // 'o'

        display.render()

        val output = outputStream.toString()
        assertTrue(output.contains("Hello"))
    }

    @Test
    fun `test clear display`() {
        display.writeToDisplayRAM(65.toByte(), 0, 0) // 'A'
        display.writeToDisplayRAM(66.toByte(), 1, 1) // 'B'

        display.clear()
        display.render()

        val output = outputStream.toString()
        // After clear, should only show empty characters
        val lines = output.split("\n")
        val displayLines = lines.filter { it.length == 8 && !it.contains("=") }

        displayLines.forEach { line ->
            assertTrue(line.all { it == '▫' })
        }
    }

    @Test
    fun `test display boundaries`() {
        // Test all corners
        display.writeToDisplayRAM(65.toByte(), 0, 0) // Top-left
        display.writeToDisplayRAM(66.toByte(), 0, 7) // Top-right
        display.writeToDisplayRAM(67.toByte(), 7, 0) // Bottom-left
        display.writeToDisplayRAM(68.toByte(), 7, 7) // Bottom-right

        display.render()

        val output = outputStream.toString()
        assertTrue(output.contains("A"))
        assertTrue(output.contains("B"))
        assertTrue(output.contains("C"))
        assertTrue(output.contains("D"))
    }

    @Test
    fun `test all valid ASCII values`() {
        for (ascii in 1..127) {
            display.clear()
            display.writeToDisplayRAM(ascii.toByte(), 0, 0)

            // Should not throw exception
            display.render()
        }
    }
}