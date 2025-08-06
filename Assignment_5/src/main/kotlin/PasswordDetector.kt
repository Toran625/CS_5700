class PasswordDetector : Detector() {

    class StartState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            when {
                input in listOf("!", "@", "#", "$", "%", "&", "*") -> detector.setState(detector.hasSpecialState)
                input[0].isUpperCase() -> detector.setState(detector.hasUpperState)
                else -> detector.setState(detector.noneMetState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class HasUpperState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                in listOf("!", "@", "#", "$", "%", "&", "*") -> detector.setState(detector.hasBothState)
                else -> detector.setState(detector.hasUpperState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class HasSpecialState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            when {
                input[0].isUpperCase() -> detector.setState(detector.hasBothState)
                else -> detector.setState(detector.hasSpecialState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class HasBothState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.hasBothState)
        }
        override fun isAcceptingState(): Boolean = true
    }

    class NoneMetState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            when {
                input in listOf("!", "@", "#", "$", "%", "&", "*") -> detector.setState(detector.hasSpecialState)
                input[0].isUpperCase() -> detector.setState(detector.hasUpperState)
                else -> detector.setState(detector.noneMetState)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class NotPasswordState(private val detector: PasswordDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.notPasswordState)
        }
        override fun isAcceptingState(): Boolean = false
    }

    val startState = StartState(this)
    val hasUpperState = HasUpperState(this)
    val hasSpecialState = HasSpecialState(this)
    val hasBothState = HasBothState(this)
    val noneMetState = NoneMetState(this)
    val notPasswordState = NotPasswordState(this)

    override fun getInitialState(): State = startState

    override fun detect(input: String): Boolean {
        if (input.length < 8) return false

        if (input.isNotEmpty() && input.last() in "!@#$%&*") return false

        return super.detect(input)
    }
}