class EmailDetector : Detector() {

    class StartState(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                " ", "@" -> detector.setState(detector.notEmailState)
                else -> detector.setState(detector.part1State)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class Part1State(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                " " -> detector.setState(detector.notEmailState)
                "@" -> detector.setState(detector.atSymbolState)
                else -> detector.setState(detector.part1State)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class AtSymbolState(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                " ", "@", "." -> detector.setState(detector.notEmailState)
                else -> detector.setState(detector.part2State)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class Part2State(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input) {
                " ", "@" -> detector.setState(detector.notEmailState)
                "." -> detector.setState(detector.dotState)
                else -> detector.setState(detector.part2State)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class DotState(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                " ", "@", "." -> detector.setState(detector.notEmailState)
                else -> detector.setState(detector.part3State)
            }
        }
        override fun isAcceptingState(): Boolean = false
    }

    class Part3State(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            when (input){
                " ", "@", "." -> detector.setState(detector.notEmailState)
                else -> detector.setState(detector.part3State)
            }
        }
        override fun isAcceptingState(): Boolean = true
    }

    class NotEmailState(private val detector: EmailDetector) : State {
        override fun processCharacter(input: String) {
            detector.setState(detector.notEmailState)
        }
        override fun isAcceptingState(): Boolean = false
    }

    val startState = StartState(this)
    val part1State = Part1State(this)
    val atSymbolState = AtSymbolState(this)
    val part2State = Part2State(this)
    val dotState = DotState(this)
    val part3State = Part3State(this)
    val notEmailState = NotEmailState(this)

    override fun getInitialState(): State = startState
}