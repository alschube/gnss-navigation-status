package com.example.gnssnavigationstatus.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.MainActivity
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.GnssDataHolder
import com.example.gnssnavigationstatus.data.Message
import com.example.gnssnavigationstatus.data.MessageDecoder
import com.example.gnssnavigationstatus.service.GnssDataUpdater
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors
import java.util.regex.Matcher
import java.util.regex.Pattern


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var checkBoxGPS: CheckBox
    private lateinit var checkBoxGAL: CheckBox
    private lateinit var checkBoxGLO: CheckBox
    private lateinit var checkBoxBDS: CheckBox

    private lateinit var checkBoxArray: Array<CheckBox>
    private lateinit var rtcmSwitch: Switch


    private lateinit var ipInputField: TextInputEditText
    private lateinit var ipInputFieldLayout: TextInputLayout
    private lateinit var connectButton: Button

    //var isChecked: Boolean? = null
    var isInstantiated: Boolean = false

    lateinit var socket: Socket
    lateinit var out: PrintWriter
    lateinit var inp: BufferedReader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Toast.makeText(context, "Lade Konfiguration.....", Toast.LENGTH_SHORT).show()
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        this.checkBoxGPS = root.findViewById(R.id.checkBox_GPS)
        this.checkBoxGAL = root.findViewById(R.id.checkBox_GAL)
        this.checkBoxGLO = root.findViewById(R.id.checkBox_GLO)
        this.checkBoxBDS = root.findViewById(R.id.checkBox_BDS)

        ipInputField = root.findViewById(R.id.ip_input_field_text)
        if(MainActivity.IP != null && MainActivity.IP.isNotEmpty()){
            ipInputField.setText(MainActivity.IP)
        }

        ipInputFieldLayout = root.findViewById(R.id.ip_input_field)
        this.connectButton = root.findViewById(R.id.connect_button)
        connectButton.setOnClickListener(View.OnClickListener { onConnectButtonClicked() })

        rtcmSwitch = root.findViewById(R.id.rtcm_switch)

        /**
        isChecked = requireActivity().getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getBoolean("switch_state", false)
        **/

        this.checkBoxArray = arrayOf(checkBoxGPS, checkBoxGLO, checkBoxBDS, checkBoxGAL)

        connect()

        rtcmSwitch.setOnClickListener {
            onSwitchChanged()
        }

        for (checkBox in checkBoxArray) {
            checkBox.setOnClickListener(View.OnClickListener {
                onCheckboxClicked(checkBox)
            })
        }
        return root
    }

    public fun connect(){
        try {
            if(GnssDataUpdater.socket.isConnected) {
                val initExecutor = Executors.newSingleThreadExecutor()
                initExecutor.execute {
                    startConnection(MainActivity.IP, 8764)
                    var msg: Message? = null
                    msg = Message(Message.MessageType.GNSS_GET, "get config")

                    val reply = sendMessage(msg.encodeToJson())
                    val replyEncoded = MessageDecoder().decodeFromJson(reply)
                    println("ReplyContent: " + replyEncoded.content)

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
                    } catch (e: RuntimeException) {
                        println("Error-------------------------------------------------")
                        e.printStackTrace()
                    }
                    stopConnection()
                    initExecutor.shutdown()
                }
                rtcmSwitch.isChecked = MainActivity.isChecked as Boolean

                while (!initExecutor.isTerminated) {
                    if (initExecutor.isTerminated) {
                        if (!isInstantiated && MainActivity.isChecked as Boolean) {
                            //this.init()
                        }
                        break;
                    }
                }
            }
        }
        catch (e: UninitializedPropertyAccessException){
            e.printStackTrace()
        }

    }

    private fun convertIntToBoolean(i: Int): Boolean {
        return i == 1
    }

    private fun onConnectButtonClicked(){

        val ipPattern : Pattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                + "|[1-9][0-9]|[0-9]))")
        if (ipPattern.matcher(ipInputField.text.toString()).matches()) {
            //Toast.makeText(context, "valid ip format", Toast.LENGTH_SHORT).show()
            ipInputFieldLayout.helperText = "Gültige IP-Adresse"
            MainActivity.IP = ipInputField.text.toString()
            MainActivity.IP.let {
                activity?.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    ?.edit()?.putString("ip", it)?.apply()
            }
            try {
                stopConnection()
            }
            catch (e: UninitializedPropertyAccessException){
                e.printStackTrace()
                println("Keine Verbindung vorhanden, die geschlossen werden könnte!")
            }
            GnssDataUpdater.ThreadUtil.runOnUiThread {
            Toast.makeText(context, "App wird neu gestartet", Toast.LENGTH_SHORT).show()
            }
            var intent: Intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        } else {
            ipInputFieldLayout.error = "Ungültige IP-Adresse"
        }
    }

    private fun onSwitchChanged() {
        var msg: Message? = null
        if (rtcmSwitch.isChecked) {
            Toast.makeText(context, "RTCM aktiviert", Toast.LENGTH_SHORT).show()
            MainActivity.isChecked = true
            msg = Message(Message.MessageType.RTCM_CONFIG, "enable rtcm")
        } else if (!rtcmSwitch.isChecked) {
            Toast.makeText(context, "RTCM deaktiviert", Toast.LENGTH_SHORT).show()
            MainActivity.isChecked = false
            msg = Message(Message.MessageType.RTCM_CONFIG, "disable rtcm")
            GnssDataHolder.resetRTCMData()
        }

        val rtcmExecutor = Executors.newSingleThreadExecutor()
        rtcmExecutor.execute {
            startConnection(MainActivity.IP, 8764)
            val msgFromBackend = sendMessage(msg!!.encodeToJson())
            println(msgFromBackend)
            val jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
            println(jsonFromMessage.type)
            if (jsonFromMessage.content.equals("RTCM NAK")) {
                GnssDataUpdater.ThreadUtil.runOnUiThread {
                    Toast.makeText(context, "Einstellung konnte nicht übernommen werden, versuche es erneut.", Toast.LENGTH_SHORT).show()
                    rtcmSwitch.isChecked = !rtcmSwitch.isChecked
                }
            }
            stopConnection()
            rtcmExecutor.shutdown()
        }

        MainActivity.isChecked?.let {
            activity?.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                ?.edit()?.putBoolean("switch_state", it)?.apply()
        }
    }

    private fun onCheckboxClicked(v: View) {
        val startExecutor = Executors.newSingleThreadExecutor()
        startExecutor.execute {
            startConnection(MainActivity.IP, 8764)
            var msg: Message? = null
            if (v is CheckBox) {
                val checked: Boolean = v.isChecked

                when (v.id) {
                    R.id.checkBox_GPS -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GPS")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GPS")
                        }
                    }
                    R.id.checkBox_BDS -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable BDS")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable BDS")
                        }
                    }
                    R.id.checkBox_GLO -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GLO")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GLO")
                        }
                    }
                    R.id.checkBox_GAL -> {
                        if (checked) {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "enable GAL")
                        } else {
                            msg = Message(Message.MessageType.GNSS_CONFIG, "disable GAL")
                        }
                    }
                }

                val msgFromBackend = sendMessage(msg!!.encodeToJson())
                println(msgFromBackend)
                val jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
                println(jsonFromMessage.type)
                if (jsonFromMessage.content == "NAK") {
                    Toast.makeText(context, "Einstellung konnte nicht übernommen werden, versuche es erneut.", Toast.LENGTH_SHORT).show()
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

        var temp = inp.readLine()
        println(temp)
        return temp
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


    fun init() {
        //send enable message only once
        val msg = Message(Message.MessageType.RTCM_CONFIG, "enable rtcm")
        val rtcmExecutor = Executors.newSingleThreadExecutor()
        rtcmExecutor.execute {
            startConnection(MainActivity.IP, 8764)
            val msgFromBackend = sendMessage(msg.encodeToJson())
            println(msgFromBackend)
            val jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
            println(jsonFromMessage.type)
            if (jsonFromMessage.content.equals("RTCM NAK")) {
                GnssDataUpdater.ThreadUtil.runOnUiThread {
                    rtcmSwitch.isChecked = !rtcmSwitch.isChecked
                }

            }
            stopConnection()
            rtcmExecutor.shutdown()
        }
        isInstantiated = true
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }
}