package com.example.gnssnavigationstatus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.gnssnavigationstatus.data.GnssData
import com.example.gnssnavigationstatus.data.GnssDataDecoder
import com.example.gnssnavigationstatus.ui.map.MapFragment
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class GnssDataUpdater : Service(), CoroutineScope{

    /** client for the communication (ktor lib)*/
    private val client = HttpClient {
        install(WebSockets)
    }

    /** job for coroutine (multi thread) */
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        async {
            listenToMessages()
        }
        return START_STICKY
    }

    private suspend fun listenToMessages() {
        this.client.ws(
                method = HttpMethod.Get,
                host = "192.168.178.44",
                port = 8765,
                path = "/socket"
        ) {
            // message that you want to send, maybe later as a lambda function
            send("Hello World!")
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val data: GnssData = GnssDataDecoder.decodeFromJson(frame.readText())

                        MapFragment.timeTextView.text = data.time
                        MapFragment.longitudeTextView.text = "${data.longitude}"
                        MapFragment.latitudeTextView.text = "${data.latitude}"
                        //MapFragment.gnssFixOKTextView.text = "${data.gnssFixOK}"
                        MapFragment.heightTextView.text = "${data.height}"
                        MapFragment.verticalAccuracyTextView.text = "${data.v_acc}"
                        MapFragment.horizontalAccuracyTextView.text = "${data.h_acc}"
                        // todo
                    }
                    is Frame.Binary -> println(frame.readBytes())
                    // after reading the information you can decide depending on the message which action to fulfill
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        this.client.close()
    }
}