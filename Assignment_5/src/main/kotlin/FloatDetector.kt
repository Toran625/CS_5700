class FloatDetector : Detector() {

    class StartState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                in "1".."9" -> detector.setState(detector.initialDigitState)
                "0" -> detector.setState(detector.initialZeroState)
                "." -> detector.setState(detector.periodState)
                else -> detector.setState(detector.notFloatState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class InitialDigitState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                in "0".."9" -> detector.setState(detector.initialDigitState)
                "." -> detector.setState(detector.periodState)
                else -> detector.setState(detector.notFloatState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class InitialZeroState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                "." -> detector.setState(detector.periodState)
                else -> detector.setState(detector.notFloatState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class PeriodState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                in "0".."9" -> detector.setState(detector.validFloatState)
                else -> detector.setState(detector.notFloatState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class ValidFloatState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                in "0".."9" -> detector.setState(detector.validFloatState)
                else -> detector.setState(detector.notFloatState)
            }
        }
        override fun isAcceptingState(): Boolean = true
    }

    class NotFloatState(private val detector: FloatDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.notFloatState)
        }
        override fun isAcceptingState(): Boolean = false
    }

    val startState = StartState(this)
    val initialDigitState = InitialDigitState(this)
    val initialZeroState = InitialZeroState(this)
    val periodState = PeriodState(this)
    val validFloatState = ValidFloatState(this)
    val notFloatState = NotFloatState(this)

    override fun getInitialState(): State = startState
}