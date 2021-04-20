package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

class Message (var msgType:MessageType, var msgContent: String){
    public enum class MessageType {
        GNSS_DATA, RTCM_CONFIG, GNSS_CONFIG
    }
    lateinit var type:MessageType
    lateinit var content:String

    fun encodeToJson(): String{
        return Gson().toJson(this)
    }

}