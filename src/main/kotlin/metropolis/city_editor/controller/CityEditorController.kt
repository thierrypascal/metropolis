package metropolis.city_editor.controller

import metropolis.shareddata.data.City
import metropolis.xtracted.controller.editor.EditorController
import metropolis.xtracted.controller.editor.ch
import metropolis.xtracted.controller.editor.get
import metropolis.xtracted.model.*
import metropolis.xtracted.repository.CrudRepository

fun CityEditorController(id: Int, repository: CrudRepository<City>, onSave: (Int) -> Unit, onAdd: (Int) -> Unit, onDelete: (Int) -> Unit): EditorController<City> {
    return EditorController(
        id = id,
        title = Message.TITLE,
        locale = ch,
        repository = repository,
        onSave = onSave,
        onAdd = onAdd,
        onDelete = onDelete,
        asData = {
            City(
                id              = id,
                name            = it[Id.NAME],
                country_code    = it[Id.COUNTRY_CODE],
                latitude        = it[Id.LATITUDE],
                longitude       = it[Id.LONGITUDE],
                feature_class   = it[Id.FEATURE_CLASS],
                feature_code    = it[Id.FEATURE_CODE],
                population      = it[Id.POPULATION],
                elevation       = it[Id.ELEVATION],
                dem             = it[Id.DEM],
                timezone        = it[Id.TIMEZONE],
            )
        },
        //TODO: add useful validations
        asAttributeList = { city -> listOf(
            (stringAttribute(
                id = Id.NAME,
                value = city.name,
                required = true,
            )),
            (stringAttribute(
                id = Id.COUNTRY_CODE,
                value = city.country_code,
                required = true,
            )),
            (doubleAttribute(
                id = Id.LATITUDE,
                value = city.latitude,
            )),
            (doubleAttribute(
                id = Id.LONGITUDE,
                value = city.longitude,
            )),
            (stringAttribute(
                id = Id.FEATURE_CLASS,
                value = city.feature_class,
            )),
            (stringAttribute(
                id = Id.FEATURE_CODE,
                value = city.feature_code,
            )),
            (doubleAttribute(
                id = Id.POPULATION,
                value = city.population,
            )),
            (doubleAttribute(
                id = Id.ELEVATION,
                value = city.elevation,
            )),
            (doubleAttribute(
                id = Id.DEM,
                value = city.dem,
            )),
            (stringAttribute(
                id = Id.TIMEZONE,
                value = city.timezone,
            )),
        ) }
    )
}

enum class Id(override val german: String, override val english: String) : AttributeId {
    NAME            ("Name"                 , "Name"),
    COUNTRY_CODE    ("Landeskürzel"        , "Country Code"),
    LATITUDE        ("Latitude"             , "Latitude"),
    LONGITUDE       ("Longitude"            , "Longitude"),
    FEATURE_CLASS   ("Merkmal Klasse"       , "Feature Class"),
    FEATURE_CODE    ("Merkmal Code"         , "Feature Code"),
    POPULATION      ("Einwohnerzahl"        , "Population"),
    ELEVATION       ("Höhe"                 , "Elevation"),
    DEM             ("DEM"                  , "DEM"),
    TIMEZONE        ("Zeitzone"             , "Timezone"),
}

private enum class Message(override val german: String, override val english: String) : Translatable {
    TITLE             ("Städte Editor"   , "City Editor"),
    TOO_HIGH          ("zu hoch"                 , "too high"),
    TOO_LOW           ("zu niedrig"              , "too low"),
    NAME_TOO_LONG     ("Name zu lang"            , "name too long"),
    NOT_A_CANTON_LIST ("keine Liste von Kantonen", "not a canton list")
}
