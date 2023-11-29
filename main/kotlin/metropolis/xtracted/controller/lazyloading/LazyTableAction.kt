package metropolis.xtracted.controller.lazyloading

import androidx.compose.ui.graphics.vector.ImageVector
import metropolis.xtracted.controller.Action

import metropolis.xtracted.model.TableColumn

sealed class LazyTableAction(
        override val name   : String,
        override val icon   : ImageVector? = null,
        override val enabled: Boolean) : Action {

    class Select(val id: Int)                                             : LazyTableAction("Select Item", null, true)
    class ToggleSortOrder<T>(val column: TableColumn<T, *>)               : LazyTableAction("Change Sort Order", null, true)
    class SetFilter<T>(val column: TableColumn<T, *>, val filter: String) : LazyTableAction("Set Filter", null, true)
    object SelectNext                                                     : LazyTableAction("Select Next Item", null, true)
    object SelectPrevious                                                 : LazyTableAction("Select Next Item", null, true)
}