package com.example.gnssnavigationstatus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
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

    val tv by lazy { findViewById<TextView>(R.id.receiveMessageTextView) }

    /** client for the communication (ktor lib)*/
    private val client = HttpClient {
        install(WebSockets)
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

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener(View.OnClickListener {
            // coroutine thread start
            launch {
                sendMessage()
            }
        })
    }

    /**
     * function for starting a websocket connection async
     * suspend keyword
     */
    private suspend fun sendMessage() {
        // setting up the client with the needed information
        client.ws(
                method = HttpMethod.Get,
                host = "192.168.178.48",
                port = 8765,
                path = "/socket"
        ) {
            // message that you want to send, maybe later as a lambda function
            send("Hello World!")

            val frame = incoming.receive()
            when (frame) {
                is Frame.Text -> tv.text = frame.readText()
                is Frame.Binary -> println(frame.readBytes())
                // after reading the information you can decide depending on the message which action to fulfill
            }
        }
    }
}
