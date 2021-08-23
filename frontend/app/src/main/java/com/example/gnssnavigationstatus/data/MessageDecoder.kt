package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

/**
 * Message decoder
 *
 * This class is used for decoding incoming messages to a message object that
 * can be interpreted.
 *
 */
class MessageDecoder {

    fun decodeFromJson(rawData: String) : Message{
        return Gson().fromJson(rawData, Message::class.java) as Message
    }
}