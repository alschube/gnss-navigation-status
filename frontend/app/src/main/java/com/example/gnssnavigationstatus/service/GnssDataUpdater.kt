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
                //val socket = Socket("192.168.178.44", 8765)
                startConnection("192.168.178.44", 8766)
                //socket.outputStream.write("Hello from the client!".toByteArray())
                //socket.outputStream.write("Bla blubb".toByteArray())
                val messageSender = Executors.newSingleThreadExecutor()
                messageSender.execute {
                    val msg = Message(Message.MessageType.GNSS_CONFIG, "Hello from the other side")
                    //socket.outputStream.write(msg.encodeToJson().toByteArray())
                    //sendMessage(msg.encodeToJson())
                    //println("iwas hässliches....................")
                    //val text = socket.inputStream.read()
                    //println("$text------------------")
                    while (true){
                        sendMessage(msg.encodeToJson())
                    }
                }

                val messageListener = Executors.newSingleThreadExecutor()
                messageListener.execute{
                    while (socket.isConnected){
                        var temp = inp.readLine()
                        println(temp)
                    }
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