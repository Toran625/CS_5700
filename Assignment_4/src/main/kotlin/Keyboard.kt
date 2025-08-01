class Keyboard {
    fun waitForInput(): Byte {
        print("Enter hex value (0-FF): ")
        val input = readLine() ?: ""
        return validateHexInput(input)
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