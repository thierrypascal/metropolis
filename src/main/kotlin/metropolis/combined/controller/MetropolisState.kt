package metropolis.combined.controller

import metropolis.shareddata.data.City
import metropolis.shareddata.data.Country
import metropolis.xtracted.controller.editor.EditorController
import metropolis.xtracted.controller.lazyloading.LazyTableController


data class MetropolisState(
    val title                       : String,
    val countriesExplorerController : LazyTableController<Country>,
    val citiesExplorerController    : LazyTableController<City>,
    val activeEditorController      : EditorController<*>,
    val isActiveEditorForCountry    : Boolean
    )
