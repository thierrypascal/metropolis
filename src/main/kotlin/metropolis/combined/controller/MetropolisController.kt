package metropolis.combined.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import metropolis.cities_explorer.controller.CitiesExplorerController
import metropolis.countries_explorer.controller.CountriesExplorerController
import metropolis.city_editor.controller.CityEditorController
import metropolis.country_editor.controller.CountryEditorController
import metropolis.shareddata.data.City
import metropolis.shareddata.data.Country
import metropolis.xtracted.controller.Action
import metropolis.xtracted.controller.Controller
import metropolis.xtracted.controller.editor.EditorController
import metropolis.xtracted.controller.lazyloading.LazyTableAction
import metropolis.xtracted.repository.CrudRepository

class MetropolisController(
    private val countriesRepository: CrudRepository<Country>,
    private val citiesRepository: CrudRepository<City>,
    ) : Controller<Action> {
    var state by mutableStateOf(
        MetropolisState(
            title                       = "Metropolis",
            countriesExplorerController = createCountriesExplorerController(countriesRepository),
            citiesExplorerController    = createCitiesExplorerController(citiesRepository),
            activeEditorController      = createCountryEditorController(countriesRepository, 12),
            isActiveEditorForCountry    = true
        )
    )

    override fun triggerAction(action: Action) {
        throw IllegalStateException("MetropolisController doesn't have actions. ")
    }

    private fun switchToCityEditor(id: Int){
        state = state.copy(activeEditorController = createCityEditorController(citiesRepository, id), isActiveEditorForCountry = false)
    }

    private fun switchToCountryEditor(id: Int){
        state = state.copy(activeEditorController = createCountryEditorController(countriesRepository, id), isActiveEditorForCountry = true)
    }

    private fun updateCountriesExplorer(id: Int) {
        //TODO: better, still bad!
        state = state.copy(countriesExplorerController = createCountriesExplorerController(countriesRepository))
        state.countriesExplorerController.triggerAction(LazyTableAction.Select(id)) //TODO: scroll to!
    }

    private fun updateCitiesExplorer(id: Int) {
        //TODO: better, still bad!
        state = state.copy(citiesExplorerController = createCitiesExplorerController(citiesRepository))
        state.citiesExplorerController.triggerAction(LazyTableAction.Select(id)) //TODO: scroll to!
    }

    private fun addCity() {
        val newCityId = citiesRepository.createKey(true)
        state = state.copy(activeEditorController = createCityEditorController(citiesRepository, newCityId), isActiveEditorForCountry = false)

        state.citiesExplorerController.triggerAction(LazyTableAction.Select(newCityId))
    }

    private fun addCountry() {
        val newCountryId = countriesRepository.createKey(false)
        state = state.copy(activeEditorController = createCountryEditorController(countriesRepository, newCountryId), isActiveEditorForCountry = true)

        state.countriesExplorerController.triggerAction(LazyTableAction.Select(newCountryId))
    }

    private fun deleteCity() {
        citiesRepository.delete(state.citiesExplorerController.state.selectedId!!)

        state = state.copy(citiesExplorerController = createCitiesExplorerController(citiesRepository))
    }

    private fun deleteCountry() {
        countriesRepository.delete(state.countriesExplorerController.state.selectedId!!)

        state = state.copy(countriesExplorerController = createCountriesExplorerController(countriesRepository))
    }

    private fun createCountriesExplorerController(countriesRepository: CrudRepository<Country>) =
        CountriesExplorerController(
            repository = countriesRepository,
            onSelectionChange = { switchToCountryEditor(it) })

    private fun createCitiesExplorerController(citiesRepository: CrudRepository<City>) =
        CitiesExplorerController(
            repository = citiesRepository,
            onSelectionChange = { switchToCityEditor(it) })

    private fun createCountryEditorController(countriesRepository: CrudRepository<Country>, id: Int): EditorController<Country> {
        return CountryEditorController(
            id = id,
            repository = countriesRepository,
            onSave = { updateCountriesExplorer(it) },
            onAdd = { addCountry() },
            onDelete = { deleteCountry() },
        )
    }

    private fun createCityEditorController(citiesRepository: CrudRepository<City>, id: Int): EditorController<City> {
        return CityEditorController(
            id = id,
            repository = citiesRepository,
            onSave = { updateCitiesExplorer(it) },
            onAdd = { addCity() },
            onDelete = { deleteCity() },
        )
    }

}