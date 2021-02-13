package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

class GnssDataDecoder {
    companion object {
        fun decodeFromJson(rawData: String) : GnssData {
            return Gson().fromJson(rawData, GnssData::class.java) as GnssData
        }
    }
}