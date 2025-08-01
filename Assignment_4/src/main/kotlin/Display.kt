class Display {
    private val displayRAM = ByteArray(64) { 0 }

    fun writeToDisplayRAM(asciiValue: Byte, row: Int, col: Int) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            throw RuntimeException("Display position out of bounds: ($row, $col)")
        }
        if (asciiValue < 0 || asciiValue > 127) {
            throw RuntimeException("Invalid ASCII value: $asciiValue")
        }

        val address = row * 8 + col
        displayRAM[address] = asciiValue
    }

    fun render() {
        println("=".repeat(8))
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val address = row * 8 + col
                val asciiValue = displayRAM[address].toInt() and 0xFF
                val char = if (asciiValue == 0) '▫' else asciiValue.toChar()
                print(char)
            }
            println()
        }
        println("=".repeat(8))
    }

    fun clear() {
        displayRAM.fill(0)
    }
}