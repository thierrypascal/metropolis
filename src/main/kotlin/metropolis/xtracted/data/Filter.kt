package metropolis.xtracted.data

data class Filter<T>(private val column: DbColumn,
                     private val op    : OP,
                     private val values: List<T>){

    fun asSql() : String = when(op)  {
        OP.BETWEEN -> "${column.name} ${op.sqlOP} '${values[0]}' AND '${values[1]}'"
        else       -> "${column.name} ${op.sqlOP} '${values[0]}'"
    }
}

fun List<Filter<*>>.asSql() : String = if(isEmpty()) "" else "WHERE ${joinToString(" AND ") { it.asSql() }}"

enum class OP(val sqlOP : String) {
    GREATER(">"),
    LESS("<"),
    EQ("="),
    BETWEEN("BETWEEN"),
    LIKE("LIKE")
}
