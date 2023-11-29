package metropolis.xtracted.controller.undo

data class Snapshot<S>(val beforeAction: S,
                       val afterAction: S)
