package com.example.gnssnavigationstatus.data

class GnssData() {

    var time: String? = null
    var longitude: Float? = null
    var latitude: Float? = null
    //var gnssFixOK Boolean? = null
    var height: Int? = null
    var verticalAccuracy: Int? = null
    var horizontalAccuracy: Int? = null
    var numSatsTotal: Int? = null
    var numSatsFixed: Int? = null

    companion object {
        @Volatile
        @JvmStatic
        private var INSTANCE: GnssData? = null

        @JvmStatic
        @JvmOverloads
        fun getInstance(): GnssData = INSTANCE ?: synchronized(this) {
            INSTANCE ?: GnssData().also {
                INSTANCE = it
            }
        }
    }
}