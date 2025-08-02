import java.io.File

class D5700Computer private constructor() {
    private val cpu = CPU()


    companion object {
        @Volatile
        private var INSTANCE: D5700Computer? = null

        fun getInstance(): D5700Computer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: D5700Computer().also { INSTANCE = it }
            }
        }
    }

    fun loadProgram(filePath: String) {
        //implement
    }

    fun start() {
        //implement
    }

}