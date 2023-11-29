package metropolis.countries_explorer.controller

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import metropolis.shareddata.data.Country
import metropolis.shareddata.repository.CountryRepository
import metropolis.xtracted.controller.lazyloading.LazyTableController
import metropolis.xtracted.model.DoubleColumn
import metropolis.xtracted.model.StringColumn
import metropolis.xtracted.repository.CrudRepository

private const val E = "..."
private val s   = 100.dp
private val m   = 150.dp
private val l   = 200.dp
private val xl  = 250.dp

fun CountriesExplorerController(repository: CrudRepository<Country>, onSelectionChange: (id: Int) -> Unit) =
    LazyTableController(
        title = "Countries of the World",
        repository = repository,
        defaultItem = Country(-999, E, E, E, 0.0, 0.0, E, E, E, E, E, E, E, E),
        onSelectionChange = onSelectionChange,
        columns = listOf(
            StringColumn(
                header          = "Name",
                width           = xl,
                alignment       = Alignment.CenterStart,
                fixed           = true,
                dbColumn        = CountryRepository.NAME,
                valueProvider   = { it.name }
            ),
            StringColumn(
                header          = "Capital",
                width           = m,
                alignment       = Alignment.CenterStart,
                fixed           = true,
                dbColumn        = CountryRepository.CAPITAL,
                valueProvider   = { it.capital }
            ),
            StringColumn(
                header          = "ISO Alpha 1 Code",
                width           = s,
                fixed           = true,
                dbColumn        = CountryRepository.ISO_ALPHA2,
                valueProvider   = { it.isoAlpha2 }
            ),
            DoubleColumn(
                header          = "Area m\u00B2",
                width           = m,
                dbColumn        = CountryRepository.AREA_IN_SQKM,
                valueProvider   = { it.area_sqm },
            ),
            DoubleColumn(
                header          = "Population",
                width           = m,
                dbColumn        = CountryRepository.POPULATION,
                valueProvider   = { it.population },
            ),
            StringColumn(
                header          = "Continent",
                width           = s,
                dbColumn        = CountryRepository.CONTINENT,
                valueProvider   = { it.continent },
            ),
            StringColumn(
                header          = "Currency Code",
                width           = s,
                dbColumn        = CountryRepository.CURRENCY_CODE,
                valueProvider   = { it.currencyCode },
            ),
            StringColumn(
                header          = "Currency",
                width           = m,
                dbColumn        = CountryRepository.CURRENCY_NAME,
                valueProvider   = { it.currencyName },
            ),
            StringColumn(
                header          = "TLD",
                width           = s,
                dbColumn        = CountryRepository.TLD,
                valueProvider   = { it.tld },
            ),
            StringColumn(
                header          = "Phone",
                width           = s,
                dbColumn        = CountryRepository.PHONE,
                valueProvider   = { it.phone },
            ),
            StringColumn(
                header          = "Postal Code Format",
                width           = s,
                dbColumn        = CountryRepository.POSTAL_CODE_FORMAT,
                valueProvider   = { it.postal_code_format },
            ),
            StringColumn(
                header          = "Languages",
                width           = xl,
                dbColumn        = CountryRepository.LANGUAGES,
                valueProvider   = { it.languages },
            ),
            StringColumn(
                header          = "Neighbours",
                width           = xl,
                dbColumn        = CountryRepository.NEIGHBOURS,
                valueProvider   = { it.neighbours },
            ),
        )
    )