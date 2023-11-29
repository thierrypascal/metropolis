package metropolis.combined.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import metropolis.cities_explorer.view.CitiesExplorerUI
import metropolis.cities_explorer.view.CountriesExplorerUI
import metropolis.city_editor.view.CityEditorUI
import metropolis.combined.controller.MetropolisState
import metropolis.country_editor.view.CountryEditorUI
import metropolis.shareddata.data.City
import metropolis.shareddata.data.Country
import metropolis.xtracted.model.EditorState

@Composable
fun ApplicationScope.MetropolisWindow(state : MetropolisState) {
    Window(title          = state.title,
           onCloseRequest = { exitApplication() },
           state          = rememberWindowState(
               placement = WindowPlacement.Maximized,
               width    = 580.dp,
               height   = 550.dp,
               position = WindowPosition(Alignment.Center))) {

        MetropolisUI(state)
    }
}

@Composable
fun MetropolisUI(state : MetropolisState) {
    with(state){
        MasterDetail(
            countriesExplorer = {
                CountriesExplorerUI(
                    state           =   countriesExplorerController.state,
                    dataProvider    = { countriesExplorerController.getData(it) },
                    idProvider      = { it.id },
                    trigger         = { countriesExplorerController.triggerAction(it) },
                )},
            citiesExplorer = {
                CitiesExplorerUI(
                    state           =   citiesExplorerController.state,
                    dataProvider    = { citiesExplorerController.getData(it) },
                    idProvider      = { it.id },
                    trigger         = { citiesExplorerController.triggerAction(it) },
                )
            },
            activeExplorer = {
                 if(state.isActiveEditorForCountry){
                    CountryEditorUI(
                        state           = activeEditorController.state as EditorState<Country>,
                        trigger         = { activeEditorController.triggerAction(it) },
                    )
                 }else{
                     CityEditorUI(
                         state           = activeEditorController.state as EditorState<City>,
                         trigger         = { activeEditorController.triggerAction(it) },
                     )
                 }
            },
        )
    }
}

@Composable
fun MasterDetail(/*toolbar:  @Composable () -> Unit = {},*/
                 countriesExplorer: @Composable () -> Unit,
                 citiesExplorer:    @Composable () -> Unit,
                 activeExplorer:    @Composable () -> Unit,
){
    val padding = 20.dp
    val elevation = 2.dp

    Column {
//        TopAppBar(backgroundColor = Color.LightGray){
//            toolbar()
//        }

        Row(modifier = Modifier.fillMaxSize()
            .padding(padding)){

            Column (modifier = Modifier.weight(1.0f)) {
                Card(elevation = elevation,
                    modifier  =  Modifier.weight(1.0f)
                        .fillMaxSize()){
                    countriesExplorer()
                }
                Spacer(Modifier.height(padding))
                Card(elevation = elevation,
                    modifier  =  Modifier.weight(1.0f)
                        .fillMaxSize()){
                    citiesExplorer()
                }
            }

            Spacer(Modifier.width(padding))

            Column (modifier = Modifier.weight(1.0f)) {
                Card(elevation = elevation,
                    modifier  =  Modifier.weight(1.0f)
                        .fillMaxSize()){
                    activeExplorer()
                }
            }
        }
    }
}
