package com.example.gnssnavigationstatus.data

class GnssData() {

    var time: String? = null
    var longitude: Float? = null
    var latitude: Float? = null
    var gnss_fix_ok: Boolean? = null
    var height: Int? = null
    var v_acc: Int? = null
    var h_acc: Int? = null

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