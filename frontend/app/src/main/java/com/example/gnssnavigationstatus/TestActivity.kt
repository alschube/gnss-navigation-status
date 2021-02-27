package com.example.gnssnavigationstatus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.gnssnavigationstatus.data.GnssData
import com.example.gnssnavigationstatus.data.GnssDataDecoder
import com.example.gnssnavigationstatus.service.GnssDataUpdater
import com.google.gson.JsonObject
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * this should work as a background service that collects the data into a temp model
 *
 * used libs, that have to be mentioned in the build.gradle(:app)
 *
 * implementation 'io.ktor:ktor-client-websockets:1.4.2'
 * implementation 'io.ktor:ktor-client-cio:1.4.2'
 * implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2'
 *
 * dont forget the permission in the manifest
 * <uses-permission android:name="android.permission.INTERNET"/>
 */
class TestActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        lateinit var tv: TextView
    }

    /** job for coroutine (multi thread) */
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        tv = findViewById(R.id.receiveMessageTextView)
        Intent(this, GnssDataUpdater::class.java).also { intent ->
            startService(intent)
        }
        //tv.text = GnssData.time
    }
}
