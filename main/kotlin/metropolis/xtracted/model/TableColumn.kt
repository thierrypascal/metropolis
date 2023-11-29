package metropolis.xtracted.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.data.Filter
import metropolis.xtracted.data.OP

sealed class TableColumn<T, V>(val header       : String,
                               val width        : Dp,
                               val alignment    : Alignment,
                               val fixed        : Boolean,
                               val dbColumn     : DbColumn?,
                               val valueProvider: (T) -> V?,
                               val formatter    : (V?) -> String){
    var filterAsText by mutableStateOf("")

    abstract fun validFilterDescription() : Boolean

    abstract fun createFilter() : Filter<V>
}

class DoubleColumn<T>(header       : String,
                      width        : Dp             = Dp.Unspecified,
                      alignment    : Alignment      = Alignment.Center,
                      fixed        : Boolean        = false,
                      dbColumn     : DbColumn?      = null,
                      valueProvider: (T) -> Double?,
                      formatter    : (Double?) -> String = { it.toString() }) : TableColumn<T, Double>(header, width, alignment, fixed, dbColumn, valueProvider, formatter){

    private val regex = "^\$|\\s*[<>]?\\s*[-+]?[0-9]+\\.?[0-9]*\\s*|\\s*[-+]?[0-9]*\\.?[0-9]*\\s*\\.\\.\\s*[-+]?[0-9]+\\.?[0-9]*\\s*".toRegex()

    override fun validFilterDescription(): Boolean = regex.matches(filterAsText)

    override fun createFilter(): Filter<Double> {
        val filter = filterAsText.trim()
        lateinit var op: OP
        lateinit var args: List<Double>
        when {
            filter.startsWith("<") -> {
                op = OP.LESS
                args = listOf(filter.substring(1).trim().toDouble())
            }
            filter.startsWith(">") -> {
                op = OP.GREATER
                args = listOf(filter.substring(1).trim().toDouble())
            }
            filter.contains("..") -> {
                op = OP.BETWEEN
                val pos = filter.indexOf("..")
                args = listOf(
                    filter.substring(0, pos).trim().toDouble(),
                    filter.substring(pos + 2).trim().toDouble()
                )
            }
            else -> {
                op = OP.EQ
                args = listOf(filter.toDouble())
            }
        }
        return Filter(dbColumn!!, op, args)
    }
}

class IntColumn<T>(header:        String,
                   width:         Dp           = Dp.Unspecified,
                   alignment:     Alignment    = Alignment.Center,
                   fixed:         Boolean      = false,
                   dbColumn:      DbColumn?    = null,
                   valueProvider: (T) -> Int?,
                   formatter :    (Int?) -> String = { it.toString() }) : TableColumn<T, Int>(header, width, alignment, fixed, dbColumn, valueProvider, formatter){

    private val regex = "^\$|\\s*[<>]?\\s*[-+]?[0-9]+\\s*|\\s*[-+]?[0-9]*\\s*\\.\\.\\s*[-+]?[0-9]+\\s*".toRegex()

    override fun validFilterDescription(): Boolean = regex.matches(filterAsText)

    override fun createFilter(): Filter<Int> {
        val filter = filterAsText.trim()
        lateinit var op: OP
        lateinit var args: List<Int>
        when {
            filter.startsWith("<") -> {
                op = OP.LESS
                args = listOf(filter.substring(1).trim().toInt())
            }
            filter.startsWith(">") -> {
                op = OP.GREATER
                args = listOf(filter.substring(1).trim().toInt())
            }
            filter.contains("..") -> {
                op = OP.BETWEEN
                val pos = filter.indexOf("..")
                args = listOf(
                    filter.substring(0, pos).trim().toInt(),
                    filter.substring(pos + 2).trim().toInt()
                )
            }
            else -> {
                op = OP.EQ
                args = listOf(filter.toInt())
            }
        }
        return Filter(dbColumn!!, op, args)
    }
}


class StringColumn<T>(header:        String,
                      width:         Dp          = Dp.Unspecified,
                      alignment:     Alignment   = Alignment.Center,
                      fixed:         Boolean     = false,
                      dbColumn:      DbColumn?   = null,
                      valueProvider: (T) -> String?,
                      formatter:     (String?) -> String = { it ?: "?" }) : TableColumn<T, String>(header, width, alignment, fixed, dbColumn, valueProvider, formatter) {

    override fun validFilterDescription(): Boolean = true

    override fun createFilter(): Filter<String> = Filter(dbColumn!!, OP.LIKE, listOf(if (filterAsText.contains("%")) filterAsText else "%$filterAsText%"))

}

