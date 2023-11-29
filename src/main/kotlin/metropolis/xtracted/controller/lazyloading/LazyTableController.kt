package metropolis.xtracted.controller.lazyloading

import java.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import metropolis.xtracted.controller.ControllerBase
import metropolis.xtracted.controller.LRUCache
import metropolis.xtracted.controller.Scheduler
import metropolis.xtracted.data.Filter
import metropolis.xtracted.data.SortDirection
import metropolis.xtracted.data.SortDirective
import metropolis.xtracted.data.UNORDERED
import metropolis.xtracted.model.TableColumn
import metropolis.xtracted.model.TableState
import metropolis.xtracted.repository.CrudRepository
import metropolis.xtracted.repository.Identifiable

class LazyTableController<T: Identifiable>(title                  : String,
                                           private val repository : CrudRepository<T>,
                                           columns                : List<TableColumn<T, *>>,
                                           private val defaultItem: T,
                                           val onSelectionChange  : (id: Int) -> Unit ) :
        ControllerBase<TableState<T>, LazyTableAction>(initialState = TableState(title            = title,
                                                                                 triggerRecompose = false,
                                                                                 allIds           = repository.readFilteredIds(emptyList(), SortDirective(null)),
                                                                                 selectedId       = null,
                                                                                 lazyListState    = LazyListState(),
                                                                                 focusRequester   = FocusRequester(),
                                                                                 currentFilters   = emptyList(),
                                                                                 currentSort      = UNORDERED,
                                                                                 filteredCount    = repository.filteredCount(emptyList()),
                                                                                 totalCount       = repository.totalCount(),
                                                                                 columns          = columns
                                                                                )
                                                         ) {

    // filtern erst nach einer gewissen 'Ruhezeit'
    private val filterScheduler = Scheduler(200)

    private val cache = Collections.synchronizedMap(LRUCache<Int, T>(100))

    fun getData(id: Int): T =
        cache.computeIfAbsent(id) {
            uiScope.launch {
                val deferred = ioScope.async {
                    delay(60) // nur zur Simulation eines langsamen DB-Zugriffs
                    repository.read(id)!!
                }
                cache[id] = deferred.await()
                if (isVisible(id)) {  //es kann sein, dass mittlerweile schon an eine andere Stelle gescrollt wurde
                    recompose()
                }
            }

            defaultItem
        }


    override fun executeAction(action: LazyTableAction) : TableState<T> =
        when (action) {
            is LazyTableAction.Select             -> changeSelection(action.id)
            is LazyTableAction.SelectNext         -> selectNext()
            is LazyTableAction.SelectPrevious     -> selectPrevious()
            is LazyTableAction.SetFilter<*>       -> setFilter(action.column as TableColumn<T, *>, action.filter)
            is LazyTableAction.ToggleSortOrder<*> -> toggleSortOrder(action.column as TableColumn<T, *>)
        }

    private fun changeSelection(id: Int): TableState<T> {
        onSelectionChange(id)
        return state.copy(selectedId = id)
    }

    private  fun selectNext() =
        with(state) {
            focusRequester.requestFocus()
            val nextIdx = (allIds.indexOf(selectedId ?: -1) + 1).coerceAtMost(filteredCount - 1)
            if (nextIdx >= lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size - 2) {
                scrollToIdx(lazyListState.firstVisibleItemIndex + 1)
            }
            changeSelection(allIds[nextIdx])
        }

    private fun selectPrevious() =
        with(state) {
            focusRequester.requestFocus()
            val nextIdx = (allIds.indexOf(selectedId ?: 1) - 1).coerceAtLeast(0)
            if (nextIdx < lazyListState.firstVisibleItemIndex) {
                scrollToIdx(lazyListState.firstVisibleItemIndex - 1)
            }
            changeSelection(allIds[nextIdx])
        }

    private fun setFilter(column: TableColumn<T, *>, filter: String) : TableState<T> {
        column.filterAsText = filter
        filterScheduler.scheduleTask {
            if (column.validFilterDescription()) {
                val allIds = repository.readFilteredIds(filters       = createFilterList(),
                                                        sortDirective = state.currentSort)
                scrollToIdx(0)
                state = state.copy(allIds        = allIds,
                                   filteredCount = allIds.size)
            }
        }
        return state
    }

    private fun toggleSortOrder(column: TableColumn<T, *>): TableState<T> {
        var nextSortDirective = UNORDERED

        if (null == column.dbColumn) {
            nextSortDirective = UNORDERED
        } else {
            val currentSortDirective = state.currentSort
            if (currentSortDirective.column == column.dbColumn) {
                if (currentSortDirective.direction == SortDirection.ASC) {
                    nextSortDirective = SortDirective(currentSortDirective.column, SortDirection.DESC)
                } else if (currentSortDirective.direction == SortDirection.DESC) {
                    nextSortDirective = UNORDERED
                }
            } else if (UNORDERED == currentSortDirective || currentSortDirective.column != column.dbColumn) {
                nextSortDirective = SortDirective(column.dbColumn, SortDirection.ASC)
            }
        }

        scrollToIdx(0)

        return state.copy(currentSort = nextSortDirective,
                          allIds      = repository.readFilteredIds(createFilterList(), nextSortDirective))

    }

    // einige Hilfsfunktionen

    private fun createFilterList(): List<Filter<*>> =
        buildList {
            state.columns.forEach {
                if (it.dbColumn != null && it.filterAsText.isNotBlank() && it.validFilterDescription()) {
                    add(it.createFilter())
                }
            }
        }

    private fun scrollToIdx(idx: Int) =
        uiScope.launch {
            state.lazyListState.scrollToItem(idx, 0)
        }

    private fun isVisible(id: Int) : Boolean =
        with(state){
            val idx = allIds.indexOf(id)
            idx >= lazyListState.firstVisibleItemIndex &&
            idx < lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size
        }

    private fun recompose() {
        state = state.copy(triggerRecompose = !state.triggerRecompose)
    }
}

