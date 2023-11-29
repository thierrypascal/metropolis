package metropolis.combined

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import metropolis.combined.controller.MetropolisController
import metropolis.combined.view.MetropolisWindow
import metropolis.shareddata.repository.cityRepository
import metropolis.shareddata.repository.countryRepository
import metropolis.xtracted.repository.urlFromResources
import metropolis.xtracted.repository.urlFromWorkingDirectory
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

fun main() {
    LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).level = Level.INFO

//    val url        = "/data/metropolisDB".urlFromResources()
    val url        = "/data/metropolisDB".urlFromWorkingDirectory()

    val countriesRepository             = countryRepository(url)
    val citiesRepository                   = cityRepository(url)

    val metropolisController                                 = MetropolisController(countriesRepository, citiesRepository)

    application {
        with(metropolisController.state){
            countriesExplorerController .initializeUiScope(rememberCoroutineScope())
            citiesExplorerController    .initializeUiScope(rememberCoroutineScope())
            activeEditorController      .initializeUiScope(rememberCoroutineScope())
        }

        MetropolisWindow(state = metropolisController.state)
    }
}