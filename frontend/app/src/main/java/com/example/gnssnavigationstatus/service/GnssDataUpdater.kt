package com.example.gnssnavigationstatus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.concurrent.Executors
import com.example.gnssnavigationstatus.data.Message
import java.io.PrintWriter


class GnssDataUpdater : Service(){

    lateinit var socket: Socket
    lateinit var out:PrintWriter
    lateinit var inp:BufferedReader

    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //private val executor: Executor
        val executor = Executors.newSingleThreadExecutor()
        executor.execute{
            try {
                startConnection("192.168.178.44", 8765)
                while (socket.isConnected) {
                    var temp = inp.readLine()
                    println(temp)
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }


        return START_STICKY
    }

    fun startConnection(ip: String, port: Int){
        this.socket = Socket(ip, port)
        out = PrintWriter(socket.getOutputStream(), true)
        inp = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    fun sendMessage(msg: String) : String{
        out.println(msg)
        return inp.readLine()
    }

    fun stopConnection(){
        inp.close()
        out.close()
        socket.close()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        //stopConnection()
    }
}