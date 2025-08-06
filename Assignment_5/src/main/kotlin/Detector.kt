abstract class Detector {
    var currentState: State = getInitialState()

    abstract fun getInitialState(): State

    open fun setState(newState: State) {
        currentState = newState
    }

    open fun detect(input: String): Boolean {
        currentState = getInitialState()
        for (char in input) {
            currentState.processCharacter(char.toString())
        }
        return currentState.isAcceptingState()
    }
}