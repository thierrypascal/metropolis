package metropolis.xtracted.repository

import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.data.Filter
import metropolis.xtracted.data.SortDirective
import java.sql.ResultSet

class LazyRepository<T>(private val url        : String,
                        private val table      : String,
                        private val dataColumns: List<DbColumn>,
                        private val idColumn   : DbColumn,
                        private val mapper     : ResultSet.() -> T) {

    fun readFilteredIds(filters: List<Filter<*>>, sortDirective: SortDirective): List<Int> =
        readIds(url           = url,
                table         = table,
                idColumn      = idColumn,
                filters       = filters,
                sortDirective = sortDirective)

    fun read(id: Int) =
        readFirst(url     = url,
                  table   = table,
                  columns = dataColumns.joinToString(),
                  where   = "$idColumn = $id",
                  map     = { mapper() })

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