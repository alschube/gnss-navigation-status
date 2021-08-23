package com.example.gnssnavigationstatus.data

/**
 * This class is used for creating Satellite data objects
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class SatelliteData {

    /** some variables for each satellite*/
    var svId: Int? = null
    var gnssId: Int? = null
    var satelliteIdentifier: String? = null
    var elevation: Int? = null
    var azimut: Int? = null
    var signalStrength: Int? = null
    var type: String? = null

    /**
     * Determine the type of the satellite by checking its id
     *
     * @param id the gnssId of this satellite
     * @return the determined type
     */
    fun determineType(id: Int?): String {
        return when (id) {
            0 -> "GPS"
            1 -> "SBAS"
            2 -> "Galileo"
            3 -> "BeiDou"
            4 -> "IMES"
            5 -> "QZSS"
            6 -> "GLONASS"
            else -> {
                "unidentified type"
            }
        }
    }

    /**
     * Create satellite identifier for a satellite
     * by combining its svId, gnssId and a unique prefix
     *
     * @param svId this satellites svId
     * @param gnssId this satellites gnssIs
     * @return the created identifier
     */
    fun createSatelliteIdentifier(svId : Int?, gnssId : Int?) : String {
        return when (gnssId) {
            0 -> "G$svId"
            1 -> "S$svId"
            2 -> "E$svId"
            3 -> "B$svId"
            4 -> "I$svId"
            5 -> "Q$svId"
            6 -> "R$svId"
            else -> {
                "unidentified type"
            }
        }
    }
}