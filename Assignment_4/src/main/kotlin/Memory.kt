class Memory {
    private val ram = ByteArray(4096)
    private val rom = ByteArray(4096)

    fun loadROM(data: ByteArray) {
        data.copyInto(rom, 0, 0, minOf(data.size, 4096))
    }

    fun readByte(address: Short, isROM: Boolean): Byte {
        val addr = address.toInt() and 0xFFFF
        if (addr >= 4096) throw RuntimeException("Memory address out of bounds: $addr")

        return if (isROM) rom[addr] else ram[addr]
    }

    fun writeByte(address: Short, value: Byte, isROM: Boolean) {
        val addr = address.toInt() and 0xFFFF
        if (addr >= 4096) throw RuntimeException("Memory address out of bounds: $addr")

        if (isROM) {
            try {
                rom[addr] = value
            } catch (e: Exception) {
                throw RuntimeException("Cannot write to ROM at address $addr")
            }
        } else {
            ram[addr] = value
        }
    }

    fun clearRAM() {
        ram.fill(0)
    }
}