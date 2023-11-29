package metropolis.country_editor

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import metropolis.country_editor.view.CountryEditorWindow
import metropolis.country_editor.controller.CountryEditorController
import metropolis.shareddata.repository.countryRepository
import metropolis.xtracted.repository.urlFromResources

fun main() {
    val url = "/data/metropolisDB".urlFromResources()
    val repository = countryRepository(url)
    val defaultId = 20

    val controller = CountryEditorController(
        defaultId,
        repository,
        { println("Saved country with ID = $it, updating CountryExplorer") },
        { println("Created new country with ID = $it, updating CountryExplorer and open CountryEditor on new country") },
        { println("Deleted country with ID = $it, updating CountryExplorer and leave CountryEditor on last opened country") })

    application {
        controller.initializeUiScope(rememberCoroutineScope())

        CountryEditorWindow(
            state   = controller.state,
            trigger = {controller.triggerAction(it)})
    }
}
