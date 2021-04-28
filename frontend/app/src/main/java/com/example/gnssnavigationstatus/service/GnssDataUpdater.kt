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
import com.example.gnssnavigationstatus.ui.table.TableFragment
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

                        MapFragment.numberSatsTextView.text = "${GnssDataHolder.numSatsFixed} (${GnssDataHolder.numSatsTotal})"
                        MapFragment.gnssFixOkTextView.text = "${GnssDataHolder.gnssFixOK}"
                        MapFragment.timeTextView.text = GnssDataHolder.time
                        MapFragment.longitudeTextView.text = "${GnssDataHolder.longitude}"
                        MapFragment.latitudeTextView.text = "${GnssDataHolder.latitude}"
                        MapFragment.heightTextView.text = "${GnssDataHolder.height?.div(1000)}"
                        MapFragment.verticalAccuracyTextView.text =
                            "${GnssDataHolder.verticalAccuracy?.div(10)}"
                        MapFragment.horizontalAccuracyTextView.text =
                            "${GnssDataHolder.horizontalAccuracy?.div(10)}"


                        SatelliteAdapter.satelliteList = SatelliteAdapter.reInit(GnssDataHolder.satellites!!)
                        TableFragment.dataList.postValue(SatelliteAdapter.satelliteList)

                    }
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