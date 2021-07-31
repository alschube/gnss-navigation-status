package com.example.gnssnavigationstatus.data

class SatelliteData {

    var svId: Int? = null
    var gnssId: Int? = null
    var satelliteIdentifier: String? = null
    var elevation: Int? = null
    var azimut: Int? = null
    var signalStrength: Int? = null
    var type: String? = null


    fun determineType(id : Int?) : String {
        when (id) {
            0 -> return "GPS"
            1 -> return "SBAS"
            2 -> return "Galileo"
            3 -> return "BeiDou"
            4 -> return "IMES"
            5 -> return "QZSS"
            6 -> return "GLONASS"
            else -> {
                return "unidentified type"
            }
        }
    }
    fun createSatelliteIdentifier(svId : Int?, gnssId : Int?) : String {
        when (gnssId) {
            0 -> return "G$svId"
            1 -> return "S$svId"
            2 -> return "E$svId"
            3 -> return "B$svId"
            4 -> return "I$svId"
            5 -> return "Q$svId"
            6 -> return "R$svId"
            else -> {
                return "unidentified type"
            }
        }
    }
}