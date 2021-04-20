package com.example.gnssnavigationstatus.data

import com.google.gson.Gson

class MessageDecoder {

    fun decodeFromJson(rawData: String) : Message{
        return Gson().fromJson(rawData, Message::class.java) as Message
    }
}