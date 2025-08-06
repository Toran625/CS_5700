class IntegerDetector : Detector() {

    class StartState(private val detector: IntegerDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                in "1".."9" -> detector.setState(detector.validIntegerState)
                else -> detector.setState(detector.invalidIntegerState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class ValidIntegerState(private val detector: IntegerDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                in "0".."9" -> detector.setState(detector.validIntegerState)
                else -> detector.setState(detector.invalidIntegerState)
            }
        }
        override fun isAcceptingState(): Boolean = true
    }

    class InvalidIntegerState(private val detector: IntegerDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.invalidIntegerState)
        }
        override fun isAcceptingState(): Boolean = false
    }

    val startState = StartState(this)
    val validIntegerState = ValidIntegerState(this)
    val invalidIntegerState = InvalidIntegerState(this)

    override fun getInitialState(): State = startState
}