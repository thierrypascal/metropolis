package metropolis.country_editor.controller

import metropolis.shareddata.data.Country
import metropolis.xtracted.controller.editor.EditorController
import metropolis.xtracted.controller.editor.ch
import metropolis.xtracted.controller.editor.get
import metropolis.xtracted.model.*
import metropolis.xtracted.repository.CrudRepository

fun CountryEditorController(id: Int, repository: CrudRepository<Country>, onSave: (Int) -> Unit, onAdd: (Int) -> Unit, onDelete: (Int) -> Unit): EditorController<Country> {
    return EditorController(
        id = id,
        title = Message.TITLE,
        locale = ch,
        repository = repository,
        onSave = onSave,
        onAdd = onAdd,
        onDelete = onDelete,
        asData = {
            Country(
                id              = id,
                name            = it[Id.NAME],
                capital         = it[Id.CAPITAL],
                isoAlpha2       = it[Id.ISO_ALPHA2],
                area_sqm        = it[Id.AREA_IN_SQKM],
                population      = it[Id.POPULATION],
                continent       = it[Id.CONTINENT],
                tld             = it[Id.TLD],
                currencyCode    = it[Id.CURRENCY_CODE],
                currencyName    = it[Id.CURRENCY_NAME],
                phone           = it[Id.PHONE],
                postal_code_format = it[Id.POSTAL_CODE_FORMAT],
                languages       = it[Id.LANGUAGES],
                neighbours      = it[Id.NEIGHBOURS],
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
                id = Id.CAPITAL,
                value = city.capital,
                required = true,
            )),
            (stringAttribute(
                id = Id.ISO_ALPHA2,
                value = city.isoAlpha2,
                required = true,
            )),
            (doubleAttribute(
                id = Id.AREA_IN_SQKM,
                value = city.area_sqm,
            )),
            (doubleAttribute(
                id = Id.POPULATION,
                value = city.population,
            )),
            (stringAttribute(
                id = Id.CONTINENT,
                value = city.continent,
            )),
            (stringAttribute(
                id = Id.CURRENCY_CODE,
                value = city.currencyCode,
            )),
            (stringAttribute(
                id = Id.CURRENCY_NAME,
                value = city.currencyName,
            )),
            (stringAttribute(
                id = Id.TLD,
                value = city.tld,
            )),
            (stringAttribute(
                id = Id.PHONE,
                value = city.phone,
            )),
            (stringAttribute(
                id = Id.POSTAL_CODE_FORMAT,
                value = city.postal_code_format,
            )),
            (stringAttribute(
                id = Id.LANGUAGES,
                value = city.languages,
            )),
            (stringAttribute(
                id = Id.NEIGHBOURS,
                value = city.neighbours,
            )),
        ) }
    )
}

enum class Id(override val german: String, override val english: String) : AttributeId {
    NAME            ("Name"                     , "Name"),
    CAPITAL         ("Hauptstadt"               , "Capital"),
    ISO_ALPHA2      ("ISO Alpha 1 Code"         , "ISO Alpha 1 Code"),
    AREA_IN_SQKM    ("Fläche m\u00B2"           , "Area m\u00B2"),
    POPULATION      ("Einwohnerzahl"            , "Population"),
    CONTINENT       ("Kontinent"                , "Continent"),
    TLD             ("TLD"                      , "TLD"),
    CURRENCY_CODE   ("Währungscode"             , "Currency Code"),
    CURRENCY_NAME   ("Währung"                  , "Currency"),
    PHONE           ("Vorwahl"                  , "Phone"),
    POSTAL_CODE_FORMAT ("Postleitzahl Format"   , "Postalcode Format"),
    LANGUAGES       ("Sprachen"                 , "Languages"),
    NEIGHBOURS      ("Nachbarsländer"           , "Neighbours"),
}

private enum class Message(override val german: String, override val english: String) : Translatable {
    TITLE             ("Länder Editor"           , "Country Editor"),
    TOO_HIGH          ("zu hoch"                 , "too high"),
    TOO_LOW           ("zu niedrig"              , "too low"),
    NAME_TOO_LONG     ("Name zu lang"            , "name too long"),
    NOT_A_CANTON_LIST ("keine Liste von Kantonen", "not a canton list")
}
