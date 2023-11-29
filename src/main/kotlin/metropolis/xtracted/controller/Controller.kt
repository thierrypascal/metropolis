package metropolis.xtracted.controller

interface  Controller<A: Action> {
    fun triggerAction(action: A)
}