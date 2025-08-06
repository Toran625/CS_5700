class BinaryDetector : Detector() {

    class StartState(private val detector: BinaryDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                "1" -> detector.setState(detector.oneState)
                else -> detector.setState(detector.notBinaryState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class OneState(private val detector: BinaryDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                "1" -> detector.setState(detector.oneState)
                "0" -> detector.setState(detector.zeroState)
                else -> detector.setState(detector.notBinaryState)
            }
        }
        override fun isAcceptingState(): Boolean = true
    }

    class ZeroState(private val detector: BinaryDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                "1" -> detector.setState(detector.oneState)
                "0" -> detector.setState(detector.zeroState)
                else -> detector.setState(detector.notBinaryState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class NotBinaryState(private val detector: BinaryDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.notBinaryState)
        }
        override fun isAcceptingState(): Boolean = false
    }

    val startState = StartState(this)
    val oneState = OneState(this)
    val zeroState = ZeroState(this)
    val notBinaryState = NotBinaryState(this)

    override fun getInitialState(): State = startState
}