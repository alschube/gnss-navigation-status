package com.example.gnssnavigationstatus.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.example.gnssnavigationstatus.data.GnssData
import com.example.gnssnavigationstatus.data.GnssDataDecoder
import com.example.gnssnavigationstatus.data.GnssDataHolder
import com.example.gnssnavigationstatus.ui.map.MapFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors


class GnssDataUpdater : Service() {

    lateinit var socket: Socket
    lateinit var out: PrintWriter
    lateinit var inp: BufferedReader

    object ThreadUtil {
        private val handler = Handler(Looper.getMainLooper())

        fun runOnUiThread(action: () -> Unit) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                handler.post(action)
            } else {
                action.invoke()
            }
        }
    }

    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //private val executor: Executor
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                startConnection("192.168.178.44", 8765)
                while (socket.isConnected) {
                    val sb = StringBuilder()
                    var temp = inp.readLine()
                    //println(temp)
                    val data: GnssData = GnssDataDecoder.decodeFromJson(temp)
                    GnssDataHolder.updateData(data)


                    ThreadUtil.runOnUiThread {

                        //println(GnssDataHolder.time)
                        MapFragment.timeTextView.text = GnssDataHolder.time
                        MapFragment.longitudeTextView.text = "${GnssDataHolder.longitude}"
                        MapFragment.latitudeTextView.text = "${GnssDataHolder.latitude}"
                        //MapFragment.gnssFixOKTextView.text = "${data.gnssFixOK}"
                        MapFragment.heightTextView.text = "${GnssDataHolder.height?.div(1000)}"
                        MapFragment.verticalAccuracyTextView.text =
                            "${GnssDataHolder.verticalAccuracy?.div(10)}"
                        MapFragment.horizontalAccuracyTextView.text =
                            "${GnssDataHolder.horizontalAccuracy?.div(10)}"

                    }


                    /*MapFragment.timeTextView.text = data.time
                    MapFragment.longitudeTextView.text = "${data.longitude}"
                    MapFragment.latitudeTextView.text = "${data.latitude}"
                    //MapFragment.gnssFixOKTextView.text = "${data.gnssFixOK}"
                    MapFragment.heightTextView.text = "${data.height?.div(1000)}"
                    MapFragment.verticalAccuracyTextView.text = "${data.verticalAccuracy?.div(10)}"
                    MapFragment.horizontalAccuracyTextView.text =
                        "${data.horizontalAccuracy?.div(10)}"*/
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        return START_STICKY
    }

    fun startConnection(ip: String, port: Int) {
        this.socket = Socket(ip, port)
        out = PrintWriter(socket.getOutputStream(), true)
        inp = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    fun sendMessage(msg: String): String {
        out.println(msg)
        return inp.readLine()
    }

    fun stopConnection() {
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