package com.example.gnssnavigationstatus.data




class GnssDataHolder {

    companion object {
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

        fun updateData(data: GnssData) {
            time = data.time
            longitude = data.longitude
            latitude = data.latitude
            gnssFixOK = data.gnssFixOK
            height = data.height
            verticalAccuracy = data.verticalAccuracy
            horizontalAccuracy = data.horizontalAccuracy
            numSatsTotal = data.numSatsTotal
            numSatsFixed = data.numSatsFixed
            satellites = data.satellites
        }
    }
}