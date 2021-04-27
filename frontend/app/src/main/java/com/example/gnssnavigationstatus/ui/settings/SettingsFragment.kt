package com.example.gnssnavigationstatus.ui.settings

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.Message
import com.example.gnssnavigationstatus.data.MessageDecoder
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.RuntimeException
import java.net.Socket
import java.util.concurrent.Executors


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var checkBoxGPS: CheckBox
    private lateinit var checkBoxGAL: CheckBox
    private lateinit var checkBoxGLO: CheckBox
    private lateinit var checkBoxBDS: CheckBox

    private lateinit var checkBoxArray: Array<CheckBox>

    private lateinit var rtcmSwitch: Switch

    lateinit var socket: Socket
    lateinit var out: PrintWriter
    lateinit var inp: BufferedReader


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        this.checkBoxGPS = root.findViewById(R.id.checkBox_GPS)
        this.checkBoxGAL = root.findViewById(R.id.checkBox_GAL)
        this.checkBoxGLO = root.findViewById(R.id.checkBox_GLO)
        this.checkBoxBDS = root.findViewById(R.id.checkBox_BDS)

        this.checkBoxArray = arrayOf(checkBoxGPS, checkBoxGLO, checkBoxBDS, checkBoxGAL)
        val initExecutor = Executors.newSingleThreadExecutor()
        initExecutor.execute {
            startConnection("192.168.178.44", 8764)
            var msg: Message? = null
            msg = Message(Message.MessageType.GNSS_GET, "get config")
            println("Message to send :" + msg.msgContent)
            var reply = sendMessage(msg!!.encodeToJson())
            //println(reply)
            var replyEncoded = MessageDecoder().decodeFromJson(reply)
            //println(replyEncoded.content)
            //var contentMap = Gson().fromJson<Map<String, Int>>(replyEncoded.content, Map.class)
            println("ReplyContent: " + replyEncoded.content)

            println("test------------------------------------------------")
            try {
                val satMap: Map<String, Int> = Gson().fromJson(
                    replyEncoded.content, object : TypeToken<HashMap<String?, Int?>?>() {}.type
                )
                println(satMap)
                Looper.prepare()
                for (checkBox in checkBoxArray) {
                    checkBox.isChecked = convertIntToBoolean(satMap[checkBox.text]!!)
                }

            } catch (e: JsonSyntaxException) {
                println("Error-------------------------------------------------")
                e.printStackTrace()
            } catch (e: RuntimeException){
                println("Error-------------------------------------------------")
                e.printStackTrace()
            }

            stopConnection()
            initExecutor.shutdown()
        }
        for (checkBox in checkBoxArray) {
            checkBox.setOnClickListener(View.OnClickListener {
                onCheckboxClicked(checkBox)
            })
        }

        this.rtcmSwitch = root.findViewById(R.id.rtcm_switch)

        return root
    }

    fun convertIntToBoolean(i: Int): Boolean {
        return i == 1
    }

    fun onCheckboxClicked(v: View) {
        val startExecutor = Executors.newSingleThreadExecutor()
        startExecutor.execute {
            startConnection("192.168.178.44", 8764)
            var msg: Message? = null
            if (v is CheckBox) {
                val checked: Boolean = v.isChecked

                when (v.id) {
                    R.id.checkBox_GPS -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GPS")
                            println("GPS something")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GPS")
                            println("GPS not something")
                        }
                    }
                    R.id.checkBox_BDS -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable BDS")
                            println("BDS something")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable BDS")
                            println("BDS not something")
                        }
                    }
                    R.id.checkBox_GLO -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GLO")
                            println("GLO something")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GLO")
                            println("GLO not something")
                        }
                    }
                    R.id.checkBox_GAL -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GAL")
                            println("GAL something")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GAL")
                            println("GAL not something")
                        }
                    }
                }

                var msgFromBackend = sendMessage(msg!!.encodeToJson())
                println(msgFromBackend)
                var jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
                println(jsonFromMessage.type)
                if (jsonFromMessage.content.equals("NAK")) {
                    v.isChecked = !v.isChecked
                }
                stopConnection()
                startExecutor.shutdown()
            }
        }
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
        try {
            inp.close()
            out.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}