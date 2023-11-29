package metropolis.shareddata.data

import metropolis.xtracted.repository.Identifiable

data class Country(
    override val id : Int,
    val name        : String?   = null,
    val isoAlpha2   : String?   = null,
    val capital     : String?   = null,
    val area_sqm    : Double?   = null,
    val population  : Double?   = null,
    val continent   : String?   = null,
    val tld         : String?   = null,
    val currencyCode: String?   = null,
    val currencyName: String?   = null,
    val phone       : String?   = null,
    val postal_code_format : String?    = null,
    val languages   : String?   = null,
    val neighbours  : String?   = null,
    //TODO: ergänzen
    ) : Identifiable
