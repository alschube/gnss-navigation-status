package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

/**
 * Gnss data decoder
 * This class decodes a Json String into a Gnss Data Object
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class GnssDataDecoder {
    companion object {
        fun decodeFromJson(rawData: String) : GnssData {
            return Gson().fromJson(rawData, GnssData::class.java) as GnssData
        }
    }
}