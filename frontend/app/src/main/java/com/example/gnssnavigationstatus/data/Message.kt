package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

/**
 * This class creates a new Message
 *
 * @property msgType the type of the message
 * @property msgContent the actual content of the message
 * @constructor Create a Message with the given type and content
 */
class Message (var msgType:MessageType, var msgContent: String){
    enum class MessageType {
        RTCM_CONFIG, GNSS_CONFIG, GNSS_GET
    }
    lateinit var type:MessageType
    lateinit var content:String

    /**
     * This method encodes this object into a json string
     *
     * @return the encoded string
     */
    fun encodeToJson(): String{
        return Gson().toJson(this)
    }

}