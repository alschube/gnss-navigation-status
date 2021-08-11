package com.example.gnssnavigationstatus.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.gnssnavigationstatus.MainActivity
import com.example.gnssnavigationstatus.data.GnssData
import com.example.gnssnavigationstatus.data.GnssDataDecoder
import com.example.gnssnavigationstatus.data.GnssDataHolder
import com.example.gnssnavigationstatus.ui.map.MapFragment
import com.example.gnssnavigationstatus.ui.map.map_components.DrawFragment
import com.example.gnssnavigationstatus.ui.table.TableFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ConnectException
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Gnss data updater class
 *
 * This service runs permanently in the background
 * and updates the textViews with new data
 *
 */
class GnssDataUpdater : Service() {

    /** create some variables for communication*/
    private lateinit var out: PrintWriter
    private lateinit var inp: BufferedReader
    private var port:Int = 8765
    companion object { lateinit var socket: Socket }

    /** create variables for fix and rtcm status*/
    private var validFix: String = ""
    private var fixType: String = ""
    private var rtcmUsed: String = ""

    /** create a ThreadUtil object for doing some things on the UI thread*/
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

    /**
    Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     *
     * @param intent The Intent supplied to {@link android.content.Context#startService},
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with {@link #stopSelfResult(int)}.
     *
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     */
    @SuppressLint("SetTextI18n")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                startConnection(MainActivity.IP, port)
                while (socket.isConnected) {
                    if(!MainActivity.isConnected && MainActivity.IP.isNotEmpty()){
                        stopConnection()
                        break
                    }
                    MapFragment.connectionStatus.setTextColor(Color.GREEN)

                    /** read incoming data and create gnss data from it*/
                    val temp = inp.readLine()
                    val data: GnssData = GnssDataDecoder.decodeFromJson(temp)
                    GnssDataHolder.updateData(data)

                    /** check whether a gnssFix is established or not and set the text and its color according to this*/
                    when(GnssDataHolder.gnssFixOK) {
                        0 -> {
                            validFix = "Kein Fix"
                            MapFragment.gnssFixOkTextView.setTextColor(Color.RED)
                        }
                        1 -> {
                            validFix = "Gültiger Fix"
                            MapFragment.gnssFixOkTextView.setTextColor(Color.GREEN)
                        }
                    }

                    /** check which type of fix is established*/
                    when(GnssDataHolder.fixType) {
                        0 -> fixType = "Kein Fix"
                        1 -> fixType = "Nur Dead Reckoning"
                        2 -> fixType = "2D-Fix"
                        3 -> fixType = "3D-Fix"
                        4 -> fixType = "Gnss + Dead Reckoning"
                        5 -> fixType = "Nur Zeit"
                    }

                    /** check if rtcm is currently used and set the text and color according to this*/
                    when(GnssDataHolder.msgUsed) {
                        2 -> {
                            rtcmUsed = "Verwendet"
                            MapFragment.rtcmStatus.setTextColor(Color.GREEN)
                        }
                        else -> {
                            rtcmUsed = "Nicht verwendet"
                            MapFragment.rtcmStatus.setTextColor(Color.RED)
                        }
                    }

                    /** updating the text of all textViews here*/
                    ThreadUtil.runOnUiThread {

                        MapFragment.numberSatsTextView.text = "${GnssDataHolder.numSatsFixed} (${GnssDataHolder.numSatsTotal})"
                        MapFragment.gnssFixOkTextView.text = validFix
                        MapFragment.fixType.text = fixType
                        MapFragment.timeTextView.text = GnssDataHolder.time
                        MapFragment.longitudeTextView.text = "${GnssDataHolder.longitude}"
                        MapFragment.latitudeTextView.text = "${GnssDataHolder.latitude}"
                        MapFragment.heightTextView.text = "${GnssDataHolder.height?.div(1000)}"
                        MapFragment.verticalAccuracyTextView.text =
                            "${GnssDataHolder.verticalAccuracy?.div(10)}"
                        MapFragment.horizontalAccuracyTextView.text =
                            "${GnssDataHolder.horizontalAccuracy?.div(10)}"
                        MapFragment.rtcmStatus.text = rtcmUsed
                        MapFragment.refStation.text = "${GnssDataHolder.refStation}"


                        SatelliteAdapter.satelliteList = SatelliteAdapter.reInit(GnssDataHolder.satellites!!)
                        TableFragment.dataList.postValue(SatelliteAdapter.satelliteList)

                        DrawFragment.map.invalidate()
                    }
                }
            } catch (e:ConnectException){
                println("Connection failed, socket could not be opened .......")
                stopService(Intent(this, this::class.java))
                MapFragment.connectionStatus.setTextColor(Color.RED)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return START_STICKY
    }

    /**
     * Start connection with given ip and port
     *
     * @param ip the ip to connect to
     * @param port the port to open
     */
    private fun startConnection(ip: String, port: Int) {
        socket = Socket(ip, port)
        out = PrintWriter(socket.getOutputStream(), true)
        inp = BufferedReader(InputStreamReader(socket.getInputStream()))
        MainActivity.isConnected = true
    }

    /**
     * Stop the connection
     *
     */
    private fun stopConnection() {
        try {
            inp.close()
            out.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        catch (e: UninitializedPropertyAccessException){
            e.printStackTrace()
            println("Cannot close connection cause it is not initialized")
        }

    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     *
     * @param intent The Intent that was used to bind to this service,
     * as given to {@link android.content.Context#bindService
     * Context.bindService}.  Note that any extras that were included with
     * the Intent at that point will <em>not</em> be seen here.
     *
     * @return Return an IBinder through which clients can call on to the
     *         service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    override fun onDestroy() {
        if (MainActivity.IP.isNotEmpty()){
            stopConnection()
        }
        startService(Intent(this, this::class.java))
    }
}