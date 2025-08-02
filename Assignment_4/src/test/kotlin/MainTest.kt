import org.junit.jupiter.api.Test

/**
 * Comprehensive test runner that executes all D5700 emulator tests
 * Run this file directly in IntelliJ to execute the entire test suite
 */
class MainTest {

    @Test
    fun runAllTests() {
        println("Running D5700 Emulator Test Suite...")

        // CPU Tests
        val cpuTest = CPUTest()
        cpuTest.setup()
        runTestClass("CPU", cpuTest) {
            cpuTest.`test initial state`()
            cpuTest.`test register operations`()
            cpuTest.`test program counter operations`()
            cpuTest.`test program counter must be even`()
            cpuTest.`test timer operations`()
            cpuTest.`test address operations`()
            cpuTest.`test memory flag operations`()
            cpuTest.`test reset`()
            cpuTest.`test execute instruction with 0000 terminates program`()
            cpuTest.`test execute instruction with program counter out of bounds`()
            cpuTest.`test execute instruction when not running`()
        }

        // Memory Tests
        val memoryTest = MemoryTest()
        memoryTest.setup()
        runTestClass("Memory", memoryTest) {
            memoryTest.`test RAM read write operations`()
            memoryTest.`test ROM read operations after loading`()
            memoryTest.`test ROM load with large data truncates to 4KB`()
            memoryTest.`test ROM write operations`()
            memoryTest.`test memory address bounds checking for RAM`()
            memoryTest.`test memory address bounds checking for ROM`()
            memoryTest.`test clearRAM operation`()
            memoryTest.`test RAM and ROM are separate`()
            memoryTest.`test address wrapping with short conversion`()
        }

        // Display Tests
        val displayTest = DisplayTest()
        displayTest.setup()
        runTestClass("Display", displayTest) {
            displayTest.`test write to display RAM within bounds`()
            displayTest.`test write to display RAM with invalid row`()
            displayTest.`test write to display RAM with invalid column`()
            displayTest.`test write to display RAM with invalid ASCII value`()
            displayTest.`test render empty display`()
            displayTest.`test render display with characters`()
            displayTest.`test clear display`()
            displayTest.`test display boundaries`()
            displayTest.`test all valid ASCII values`()
        }
        displayTest.tearDown()

        // Keyboard Tests
        val keyboardTest = KeyboardTest()
        keyboardTest.setup()
        runTestClass("Keyboard", keyboardTest) {
            keyboardTest.`test valid single hex digit input`()
            keyboardTest.`test valid two hex digit input`()
            keyboardTest.`test lowercase hex input`()
            keyboardTest.`test mixed case hex input`()
            keyboardTest.`test empty input returns zero`()
            keyboardTest.`test invalid input returns zero`()
            keyboardTest.`test input longer than two characters truncated`()
            keyboardTest.`test input with special characters returns zero`()
            keyboardTest.`test boundary hex values`()
            keyboardTest.`test all valid hex digits`()
            keyboardTest.`test prompt message is displayed`()
            keyboardTest.`test invalid input shows error message`()
        }
        keyboardTest.tearDown()

        // Instruction Factory Tests
        val factoryTest = InstructionFactoryTest()
        factoryTest.setup()
        runTestClass("InstructionFactory", factoryTest) {
            factoryTest.`test create Store instruction`()
            factoryTest.`test create Add instruction`()
            factoryTest.`test create Sub instruction`()
            factoryTest.`test create Read instruction`()
            factoryTest.`test create Write instruction`()
            factoryTest.`test create Jump instruction`()
            factoryTest.`test create ReadKeyboard instruction`()
            factoryTest.`test create SwitchMemory instruction`()
            factoryTest.`test create SkipEqual instruction`()
            factoryTest.`test create SkipNotEqual instruction`()
            factoryTest.`test create SetA instruction`()
            factoryTest.`test create SetT instruction`()
            factoryTest.`test create ReadT instruction`()
            factoryTest.`test create ConvertToBase10 instruction`()
            factoryTest.`test create ConvertByteToAscii instruction`()
            factoryTest.`test create Draw instruction`()
            factoryTest.`test unknown opcode throws exception`()
            factoryTest.`test negative opcode throws exception`()
            factoryTest.`test all valid opcodes create different instruction types`()
        }

        println("✅ All tests completed!")
    }

    private fun runTestClass(className: String, testInstance: Any, tests: () -> Unit) {
        println("Running $className tests...")
        try {
            tests()
            println("✅ $className tests passed")
        } catch (e: Exception) {
            println("❌ $className tests failed: ${e.message}")
            throw e
        }
    }
}