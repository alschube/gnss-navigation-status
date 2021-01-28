package com.example.gnssnavigationstatus.model

data class Satellite(var id: Int, var type: String, var azim: Int, var elev: Int, var signalQuality: Int)