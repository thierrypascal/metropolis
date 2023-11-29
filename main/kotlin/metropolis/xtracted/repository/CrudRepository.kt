package metropolis.xtracted.repository

import java.sql.ResultSet
import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.data.Filter
import metropolis.xtracted.data.SortDirective

class CrudRepository<D: Identifiable>(private val url        : String,
                                      private val table      : String,
                                      private val idColumn   : DbColumn,
                                      private val dataColumns: Map<DbColumn, (D) -> String?>, //maps DbColumn to cell value of data
                                      private val mapper     : ResultSet.() -> D) {

    fun createKey(isCity: Boolean) : Int {
        if (isCity) {
            return insertAndCreateKey(
                url = url,
                insertStmt = """INSERT INTO $table 
                               |        (NAME,       COUNTRY_CODE,   LATITUDE,   LONGITUDE,  FEATURE_CLASS,  FEATURE_CODE,   POPULATION, ELEVATION,  DEM, TIMEZONE,  MODIFICATION_DATE)
                               | VALUES ('New City', '...',          0.0,        0.0,        '...',          '...',          0.0,        0.0,        0.0, '...',     '...')
                           """.trimMargin()
            )
        } else {
            return insertAndCreateKey(
                url = url,
                insertStmt = """INSERT INTO $table 
                               |        (NAME,           CAPITAL, ISO_ALPHA2, ISO_ALPHA3, AREA_IN_SQKM,  POPULATION, CONTINENT, TLD,     CURRENCY_CODE,  CURRENCY_NAME,  PHONE, POSTAL_CODE_FORMAT,  LANGUAGES,  NEIGHBOURS, GEONAME_ID)
                               | VALUES ('New Country',  '...',   '...',      '...',      0.0,           0.0,        '...',     '...',   '...',          '...',          '...', '...',               '...',      '...',      0.0)
                           """.trimMargin()
            )
        }
    }

    fun readFilteredIds(filters: List<Filter<*>>, sortDirective: SortDirective): List<Int> =
        readIds(url       = url,
            table         = table,
            idColumn      = idColumn,
            filters       = filters,
            sortDirective = sortDirective)

    fun read(id: Int) : D?  =
        readFirst(url     = url,
                  table   = table,
                  columns = "$idColumn, " + dataColumns.keys.joinToString(),
                  where   = "$idColumn = $id",
                  map     = { mapper() })

    fun update(data: D){
        val valueUpdates = StringBuilder()
        dataColumns.entries.forEachIndexed { index, entry ->
            valueUpdates.append(entry.key.name)
            valueUpdates.append(" = ")
            valueUpdates.append(entry.value(data))
            if(index < dataColumns.size - 1){
                valueUpdates.append(", ")
            }

        }
        update(
            url = url,
            table = table,
            id = data.id,
            idName = if (table == "COUNTRY") "ISO_NUMERIC" else "ID",
            setStatement = """SET $valueUpdates """
        )

    }

    fun delete(id: Int) =
        delete(
            url = url,
            table = table,
            id = id
        )

    fun totalCount() =
        count(url    = url,
            table  = table,
            idName = idColumn.name)

    fun filteredCount(filters: List<Filter<*>>) =
        count(url     = url,
            table   = table,
            idName  = idColumn.name,
            filters = filters)

}
