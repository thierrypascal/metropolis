package metropolis.shareddata.data

import metropolis.xtracted.repository.Identifiable

data class City(
    override val id     : Int,
    val name            : String?   = null,
    val country_code    : String?   = null,
    val latitude        : Double?   = null,
    val longitude       : Double?   = null,
    val feature_class   : String?   = null,
    val feature_code    : String?   = null,
    val population      : Double?   = null,
    val elevation       : Double?   = null,
    val dem             : Double?   = null,
    val timezone        : String?   = null,
    //TODO: ergänzen
    ) : Identifiable
