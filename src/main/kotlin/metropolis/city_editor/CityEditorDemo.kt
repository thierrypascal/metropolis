package metropolis.city_editor

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import metropolis.city_editor.controller.CityEditorController
import metropolis.city_editor.view.CityEditorWindow
import metropolis.shareddata.repository.cityRepository
import metropolis.xtracted.repository.urlFromResources

fun main() {
    val url = "/data/metropolisDB".urlFromResources()
    val repository = cityRepository(url)
    val defaultId = 105252

    val controller = CityEditorController(
        defaultId,
        repository,
        { println("Saved city with ID = $it, updating CityExplorer") },
        { println("Created new city with ID = $it, updating CityExplorer and open CityEditor on new city") },
        { println("Deleted city with ID = $it, updating CityExplorer and leave CityEditor on last opened city") })

    application {
        controller.initializeUiScope(rememberCoroutineScope())

        CityEditorWindow(
            state   = controller.state,
            trigger = {controller.triggerAction(it)})
    }
}
