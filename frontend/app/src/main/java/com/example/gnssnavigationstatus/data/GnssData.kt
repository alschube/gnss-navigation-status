package com.example.gnssnavigationstatus.data

/**
 * class for creating Gnss data objects
 *
 * @constructor Create empty Gnss data
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class GnssData{

    var time: String? = null
    var longitude: Float? = null
    var latitude: Float? = null
    var gnssFixOK: Int? = null
    var fixType: Int? = null
    var height: Int? = null
    var verticalAccuracy: Int? = null
    var horizontalAccuracy: Int? = null
    var numSatsTotal: Int? = null
    var numSatsFixed: Int? = null
    var msgUsed: Int? = null
    var refStation: Int? = null
    var satellites: List<SatelliteData>? = null

}