import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class MemoryTest {
    private lateinit var memory: Memory

    @BeforeEach
    fun setup() {
        memory = Memory()
    }

    @Test
    fun `test RAM read write operations`() {
        val address = 0x100.toShort()
        val value = 0x42.toByte()
        
        memory.writeByte(address, value, false)
        val readValue = memory.readByte(address, false)
        
        assertEquals(value, readValue)
    }

    @Test
    fun `test ROM read operations after loading`() {
        val romData = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        memory.loadROM(romData)
        
        assertEquals(0x12.toByte(), memory.readByte(0, true))
        assertEquals(0x34.toByte(), memory.readByte(1, true))
        assertEquals(0x56.toByte(), memory.readByte(2, true))
        assertEquals(0x78.toByte(), memory.readByte(3, true))
    }

    @Test
    fun `test ROM load with large data truncates to 4KB`() {
        val largeRomData = ByteArray(5000) { it.toByte() }
        memory.loadROM(largeRomData)
        
        // Should only load first 4096 bytes
        assertEquals(0.toByte(), memory.readByte(0, true))
        assertEquals(1.toByte(), memory.readByte(1, true))
        assertEquals((4095).toByte(), memory.readByte(4095, true))
    }

    @Test
    fun `test ROM write operations`() {
        val address = 0x100.toShort()
        val value = 0x42.toByte()
        
        // ROM write should work (for future-proofing writeable ROMs)
        memory.writeByte(address, value, true)
        val readValue = memory.readByte(address, true)
        
        assertEquals(value, readValue)
    }

    @Test
    fun `test memory address bounds checking for RAM`() {
        assertThrows<RuntimeException> {
            memory.readByte(4096, false)
        }
        
        assertThrows<RuntimeException> {
            memory.writeByte(4096, 0x42.toByte(), false)
        }
        
        assertThrows<RuntimeException> {
            memory.readByte((-1).toShort(), false)
        }
    }

    @Test
    fun `test memory address bounds checking for ROM`() {
        assertThrows<RuntimeException> {
            memory.readByte(4096, true)
        }
        
        assertThrows<RuntimeException> {
            memory.writeByte(4096, 0x42.toByte(), true)
        }
    }

    @Test
    fun `test clearRAM operation`() {
        memory.writeByte(0x100.toShort(), 0x42.toByte(), false)
        memory.writeByte(0x200.toShort(), 0x84.toByte(), false)
        
        memory.clearRAM()
        
        assertEquals(0.toByte(), memory.readByte(0x100.toShort(), false))
        assertEquals(0.toByte(), memory.readByte(0x200.toShort(), false))
    }

    @Test
    fun `test RAM and ROM are separate`() {
        val address = 0x100.toShort()
        val ramValue = 0x42.toByte()
        val romValue = 0x84.toByte()
        
        memory.writeByte(address, ramValue, false) // Write to RAM
        memory.writeByte(address, romValue, true)  // Write to ROM
        
        assertEquals(ramValue, memory.readByte(address, false))
        assertEquals(romValue, memory.readByte(address, true))
    }

    @Test
    fun `test address wrapping with short conversion`() {
        val address = 0xFFFF.toShort() // This becomes -1 as signed short
        
        // Should handle negative addresses correctly (convert to positive)
        assertThrows<RuntimeException> {
            memory.readByte(address, false)
        }
    }
}