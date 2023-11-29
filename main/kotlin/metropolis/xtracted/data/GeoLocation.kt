package metropolis.xtracted.data

import java.util.*

data class GeoLocation (val longitude: Double  = 0.0,
                        val latitude : Double  = 0.0,
                        val altitude : Double? = null){

    fun dms(): String = dms(latitude, longitude)

    fun asOpenStreetMapsURL(zoom : Int = 17) = "https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=${zoom}/${latitude}/${longitude}"

    private fun dms(latitude: Double, longitude: Double): String {
        val latCompassDirection = if (latitude > 0.0) "N" else "S"
        val lonCompassDirection = if (longitude > 0.0) "E" else "W"

        return "%s%s, %s%s".format(
                dms(latitude), latCompassDirection,
                dms(longitude), lonCompassDirection)
    }

    private fun dms(value: Double?): String {
        if(null != value){
            val absValue = Math.abs(value)
            val degree   = absValue.toInt()
            val minutes  = ((absValue - degree) * 60.0).toInt()
            val seconds  = (absValue - degree - minutes / 60.0) * 3600.0

            return "%02d°%02d′%02.0f″".format(Locale.ENGLISH, degree, minutes, seconds)
        }
        else {
            return ".."
        }
    }
}