package com.example.gnssnavigationstatus.data

data class GnssData(var time: String, var longitude: Float, var latitude: Float, var gnssFixOK: Boolean, var height: Int, var vAcc: Int, var hAcc: Int) {
    companion object {
        @Volatile
        @JvmStatic
        private var INSTANCE: GnssData? = null

        @JvmStatic
        @JvmOverloads
        fun getInstance(time: String, longitude: Float, latitude: Float, gnssFixOK: Boolean, height: Int, vAcc: Int, hAcc: Int): GnssData = INSTANCE ?: synchronized(this) {
            INSTANCE ?: GnssData(time, longitude, latitude, gnssFixOK, height, vAcc, hAcc).also {
                INSTANCE = it
            }
        }
    }
}