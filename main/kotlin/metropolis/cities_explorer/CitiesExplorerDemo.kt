package metropolis.cities_explorer

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import metropolis.cities_explorer.controller.CitiesExplorerController
import metropolis.cities_explorer.view.CitiesExplorerWindow
import metropolis.shareddata.repository.cityRepository
import metropolis.xtracted.repository.urlFromResources

fun main() {
    LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).level = Level.INFO

    val url        = "/data/metropolisDB".urlFromResources()
    val repository = cityRepository(url)
    val controller = CitiesExplorerController(repository) { println("Open CityEditor with ID = $it") }

    application {
        with(controller){
            initializeUiScope(rememberCoroutineScope())
            CitiesExplorerWindow(state        = state,
                                  dataProvider = { getData(it) },
                                  idProvider   = { it.id },
                                  trigger      = { triggerAction(it)}
                                 )
        }
    }
}