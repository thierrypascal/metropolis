package metropolis.xtracted.controller.undo

import metropolis.xtracted.model.UndoState


class UndoController<S> {

    private val undoStack = ArrayDeque<Snapshot<S>>()
    private val redoStack = ArrayDeque<Snapshot<S>>()

    val undoState
    get() = UndoState(undoStack.isNotEmpty(), redoStack.isNotEmpty())


    fun pushOnUndoStack(snapshot: Snapshot<S>) {
        undoStack.push(snapshot)
        redoStack.clear()
    }

    fun undo() : S? = if(undoState.undoAvailable){
                          val snapshot = undoStack.pop()
                          redoStack.push(snapshot)

                          snapshot.beforeAction
                      } else {
                          null
                      }

    fun redo() : S? = if(undoState.redoAvailable){
                          val snapshot = redoStack.pop()
                          undoStack.push(snapshot)

                          snapshot.afterAction
                      } else {
                          null
                      }


    private fun <S> ArrayDeque<S>.pop() : S = removeFirst()
    private fun <S> ArrayDeque<S>.push(element: S) = addFirst(element)

}

