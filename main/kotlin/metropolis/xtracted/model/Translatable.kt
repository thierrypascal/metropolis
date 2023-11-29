package metropolis.xtracted.model

import java.util.*

interface Translatable {
    val german: String
    val english: String

    fun translate(locale: Locale) : String = if(locale.language == "de") german else english
}