package metropolis.xtracted.data


data class SortDirective(val column: DbColumn?, val direction: SortDirection = SortDirection.ASC)

enum class SortDirection {
    ASC, DESC
}

val UNORDERED = SortDirective(null, SortDirection.ASC)