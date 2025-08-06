interface State {
    fun processCharacter(input: String)
    fun isAcceptingState(): Boolean
}