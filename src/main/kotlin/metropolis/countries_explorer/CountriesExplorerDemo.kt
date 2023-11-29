package metropolis.countries_explorer

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import metropolis.countries_explorer.controller.CountriesExplorerController
import metropolis.cities_explorer.view.CountriesExplorerWindow
import metropolis.shareddata.repository.countryRepository
import metropolis.xtracted.repository.urlFromResources

fun main() {
    LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).level = Level.INFO

    val url        = "/data/metropolisDB".urlFromResources()
    val repository = countryRepository(url)
    val controller = CountriesExplorerController(repository) { println("Open CountryEditor with ID = $it") }

    application {
        with(controller){
            initializeUiScope(rememberCoroutineScope())
            CountriesExplorerWindow(state        = state,
                                  dataProvider = { getData(it) },
                                  idProvider   = { it.id },
                                  trigger      = { triggerAction(it)}
                                 )
        }
    }
}