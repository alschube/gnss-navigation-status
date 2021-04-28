package com.example.gnssnavigationstatus.data

import java.util.ArrayList
import kotlin.properties.Delegates

class GnssData{

    var time: String? = null
    var longitude: Float? = null
    var latitude: Float? = null
    var gnssFixOK: Int? = null
    var height: Int? = null
    var verticalAccuracy: Int? = null
    var horizontalAccuracy: Int? = null
    var numSatsTotal: Int? = null
    var numSatsFixed: Int? = null
    var satellites: List<SatelliteData>? = null

}