package metropolis.cities_explorer.controller

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import metropolis.shareddata.data.City
import metropolis.shareddata.repository.CityRepository
import metropolis.xtracted.controller.lazyloading.LazyTableController
import metropolis.xtracted.model.DoubleColumn
import metropolis.xtracted.model.StringColumn
import metropolis.xtracted.repository.CrudRepository

private const val E = "..."
private val s   = 100.dp
private val m   = 150.dp
private val l   = 200.dp
private val xl  = 250.dp

fun CitiesExplorerController(repository: CrudRepository<City>, onSelectionChange: (id: Int) -> Unit) =
    LazyTableController(
        title = "Cities of the World",
        repository = repository,
        onSelectionChange = onSelectionChange,
        defaultItem = City(-999, E, E, 0.0, 0.0, E, E, 0.0, 0.0, 0.0, E),
        columns = listOf(
            StringColumn(
                header          = "Name",
                width           = xl,
                alignment       = Alignment.CenterStart,
                fixed           = true,
                dbColumn        = CityRepository.NAME,
                valueProvider   = { it.name }
            ),
            StringColumn(
                header          = "Country Code",
                width           = s,
                fixed           = true,
                dbColumn        = CityRepository.COUNTRY_CODE,
                valueProvider   = { it.country_code }
            ),
            DoubleColumn(
                header          = "Latitude",
                width           = m,
                dbColumn        = CityRepository.LATITUDE,
                valueProvider   = { it.latitude }
            ),
            DoubleColumn(
                header          = "Longitude",
                width           = m,
                dbColumn        = CityRepository.LONGITUDE,
                valueProvider   = { it.longitude }
            ),
            StringColumn(
                header          = "Feature Class",
                width           = s,
                dbColumn        = CityRepository.FEATURE_CLASS,
                valueProvider   = { it.feature_class }
            ),
            StringColumn(
                header          = "Feature Code",
                width           = s,
                dbColumn        = CityRepository.FEATURE_CODE,
                valueProvider   = { it.feature_code }
            ),
            DoubleColumn(
                header          = "Population",
                width           = s,
                dbColumn        = CityRepository.POPULATION,
                valueProvider   = { it.population }
            ),
            DoubleColumn(
                header          = "Elevation",
                width           = s,
                dbColumn        = CityRepository.ELEVATION,
                valueProvider   = { it.elevation }
            ),
            DoubleColumn(
                header          = "DEM",
                width           = m,
                dbColumn        = CityRepository.DEM,
                valueProvider   = { it.dem }
            ),
            StringColumn(
                header          = "Timezone",
                width           = xl,
                dbColumn        = CityRepository.TIMEZONE,
                valueProvider   = { it.timezone }
            ),
        )
    )