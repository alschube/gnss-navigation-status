package com.example.gnssnavigationstatus.data




class GnssDataHolder {

    companion object {
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

        fun updateData(data: GnssData) {
            time = data.time
            longitude = data.longitude
            latitude = data.latitude
            gnssFixOK = data.gnssFixOK
            fixType = data.fixType
            height = data.height
            verticalAccuracy = data.verticalAccuracy
            horizontalAccuracy = data.horizontalAccuracy
            numSatsTotal = data.numSatsTotal
            numSatsFixed = data.numSatsFixed
            msgUsed = data.msgUsed
            refStation = data.refStation
            satellites = data.satellites
        }

        fun resetRTCMData(){
            msgUsed = 0
            refStation = 0
        }
    }
}