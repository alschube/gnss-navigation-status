package com.example.gnssnavigationstatus.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
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
import java.util.regex.Pattern

/**
 * Settings fragment
 *
 * The third fragment
 * This is responsible for settings that affect satellites, rtcm and the connection to the backend
 *
 * The user can configure which Satellite Systems should be used for calculation,
 * the user can enable or disable rtcm and
 * changing the ip address of the rover (this is needed for successfully connect to the backend at least once,
 * the given ip address is then stored in shared preferences, that on the next startup it can
 * connect immediately
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class SettingsFragment : Fragment() {

    /** create the four checkboxes for the gnss*/
    private lateinit var checkBoxGPS: CheckBox
    private lateinit var checkBoxGAL: CheckBox
    private lateinit var checkBoxGLO: CheckBox
    private lateinit var checkBoxBDS: CheckBox

    private lateinit var checkBoxArray: Array<CheckBox>
    private lateinit var rtcmSwitch: Switch
    private lateinit var dialog: AlertDialog
    //private lateinit var prefs:SharedPreferences

    /** create a text input field and a connect button for the ip address functionality*/
    private lateinit var ipInputField: TextInputEditText
    private lateinit var ipInputFieldLayout: TextInputLayout
    private lateinit var connectButton: Button

    private var isInstantiated: Boolean = false

    /** create some variables for communication*/
    private lateinit var socket: Socket
    private lateinit var out: PrintWriter
    private lateinit var inp: BufferedReader

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        /** if connection is established show progress dialog while loading satellite configuration*/
        if(MainActivity.isConnected){
            GnssDataUpdater.ThreadUtil.runOnUiThread{
                showProgressDialog("Lade Satellitenkonfiguration", "Bitte warten, bis die aktuelle Konfiguration des Rovers geladen ist...")
            }
        }

        // initialize checkboxes
        this.checkBoxGPS = root.findViewById(R.id.checkBox_GPS)
        this.checkBoxGAL = root.findViewById(R.id.checkBox_GAL)
        this.checkBoxGLO = root.findViewById(R.id.checkBox_GLO)
        this.checkBoxBDS = root.findViewById(R.id.checkBox_BDS)

        ipInputField = root.findViewById(R.id.ip_input_field_text)
        if(MainActivity.IP.isNotEmpty()){
            ipInputField.setText(MainActivity.IP)
        }

        ipInputFieldLayout = root.findViewById(R.id.ip_input_field)
        this.connectButton = root.findViewById(R.id.connect_button)
        connectButton.setOnClickListener { onConnectButtonClicked() }

        rtcmSwitch = root.findViewById(R.id.rtcm_switch)

        this.checkBoxArray = arrayOf(checkBoxGPS, checkBoxGLO, checkBoxBDS, checkBoxGAL)

        /** try to connect to backend*/
        connect()

        // add onClickListener to switch and checkboxes
        rtcmSwitch.setOnClickListener {
            onSwitchChanged()
        }
        for (checkBox in checkBoxArray) {
            checkBox.setOnClickListener {
                onCheckboxClicked(checkBox)
            }
        }

        return root
    }

    /**
     * Connect to the backend
     *
     */
    fun connect(){
        try {
            if(GnssDataUpdater.socket.isConnected) {
                val initExecutor = Executors.newSingleThreadExecutor()
                initExecutor.execute {
                    startConnection(MainActivity.IP, 8764)

                    //create a Message
                    val msg: Message?
                    msg = Message(Message.MessageType.GNSS_GET, "get config")

                    // send it to the backend and receive the reply
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
                    // finally stop the connection and terminate this thread
                    stopConnection()
                    initExecutor.shutdown()
                    isInstantiated = true
                    if (dialog.isShowing){
                        GnssDataUpdater.ThreadUtil.runOnUiThread { dismissDialog() }
                    }
                }
                rtcmSwitch.isChecked = MainActivity.isChecked as Boolean
            }
        }
        catch (e: UninitializedPropertyAccessException){
            e.printStackTrace()
            println("Connection failed, socket could not be opened .....")
        }

    }

    /**
     * Convert int to boolean
     *
     * @param i the int to convert
     * @return the boolean
     */
    private fun convertIntToBoolean(i: Int): Boolean {
        return i == 1
    }

    /**
     * On connect button clicked
     *
     * This method is called, when the connect button is clicked
     * it is used for validating and saving the typed ip address
     * and for restarting the connection if has changed
     */
    private fun onConnectButtonClicked(){
        //first check if some changes were made
        if(ipInputField.text.toString() != MainActivity.IP) {
            // this regex pattern is for validating the given ip
            val ipPattern: Pattern = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))"
            )
            if (ipPattern.matcher(ipInputField.text.toString()).matches()) {
                //if regex did match
                ipInputFieldLayout.helperText = "Gültige IP-Adresse"

                //store ip in shared preferences
                MainActivity.IP = ipInputField.text.toString()
                MainActivity.prefs.edit().putString("ip", ipInputField.text.toString()).apply()

                // stop the current connection if there is one
                if (MainActivity.isConnected) {
                    stopConnection()
                }

                //inform the user
                GnssDataUpdater.ThreadUtil.runOnUiThread {
                    Toast.makeText(context, "App wird neu gestartet", Toast.LENGTH_SHORT).show()
                }

                // restart
                MainActivity.isConnected = false
                var intent: Intent = Intent(context, MainActivity::class.java)
                startActivity(intent)

            } else { // regex did not match
                ipInputFieldLayout.error = "Ungültige IP-Adresse"
            }
        }
        else {
            GnssDataUpdater.ThreadUtil.runOnUiThread { Toast.makeText(context, "IP Adresse wurde nicht geändert!", Toast.LENGTH_SHORT).show() }
        }
    }


    /**
     * This is called when the rtcm switch is changed
     * it then builds a connection and sends a message to the backend
     * with the set rtcm status
     *
     */
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

        // create a new thread for connecting and sending
        val rtcmExecutor = Executors.newSingleThreadExecutor()
        rtcmExecutor.execute {
            startConnection(MainActivity.IP, 8764)
            val msgFromBackend = sendMessage(msg!!.encodeToJson())
            println(msgFromBackend)
            val jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
            println(jsonFromMessage.type)
            if (jsonFromMessage.content == "RTCM NAK") {
                GnssDataUpdater.ThreadUtil.runOnUiThread {
                    Toast.makeText(context, "Einstellung konnte nicht übernommen werden, versuche es erneut.", Toast.LENGTH_SHORT).show()
                    rtcmSwitch.isChecked = !rtcmSwitch.isChecked
                }
            }
            stopConnection()
            rtcmExecutor.shutdown()
        }

        // save the current configuration to shared preferences
        MainActivity.prefs.edit().putBoolean("switch_state", MainActivity.isChecked!!).apply()
    }

    /**
     * This is called when a checkbox is clicked
     * it checks which one was clicked and if it was enabled or disabled
     * and sends a message according to that
     *
     * @param v the clicked checkbox
     */
    private fun onCheckboxClicked(v: View) {
        val startExecutor = Executors.newSingleThreadExecutor()
        startExecutor.execute {
            GnssDataUpdater.ThreadUtil.runOnUiThread{
               showProgressDialog("Sende Nachricht", "Bitte warten bis die Nachricht gesendet wurde...")
            }
            startConnection(MainActivity.IP, 8764)
            var msg: Message? = null
            if (v is CheckBox) {
                val checked: Boolean = v.isChecked

                when (v.id) {
                    R.id.checkBox_GPS -> {
                        msg = if (checked) {
                            Message(Message.MessageType.GNSS_CONFIG, "enable GPS")
                        } else {
                            Message(Message.MessageType.GNSS_CONFIG, "disable GPS")
                        }
                    }
                    R.id.checkBox_BDS -> {
                        msg = if (checked) {
                            Message(Message.MessageType.GNSS_CONFIG, "enable BDS")
                        } else {
                            Message(Message.MessageType.GNSS_CONFIG, "disable BDS")
                        }
                    }
                    R.id.checkBox_GLO -> {
                        msg = if (checked) {
                            Message(Message.MessageType.GNSS_CONFIG, "enable GLO")
                        } else {
                            Message(Message.MessageType.GNSS_CONFIG, "disable GLO")
                        }
                    }
                    R.id.checkBox_GAL -> {
                        msg = if (checked) {
                            Message(Message.MessageType.GNSS_CONFIG, "enable GAL")
                        } else {
                            Message(Message.MessageType.GNSS_CONFIG, "disable GAL")
                        }
                    }
                }

                val msgFromBackend = sendMessage(msg!!.encodeToJson())
                println(msgFromBackend)
                val jsonFromMessage = MessageDecoder().decodeFromJson(msgFromBackend)
                println(jsonFromMessage.type)

                //if NAK is the reply then something went wrong
                if (jsonFromMessage.content == "NAK") {
                    GnssDataUpdater.ThreadUtil.runOnUiThread {Toast.makeText(context, "Einstellung konnte nicht übernommen werden, versuche es erneut.", Toast.LENGTH_SHORT).show()}
                    v.isChecked = !v.isChecked
                }

                //finally stop connection and shutdown the executor
                stopConnection()
                startExecutor.shutdown()
                GnssDataUpdater.ThreadUtil.runOnUiThread { dismissDialog()}
            }
        }
    }

    /**
     * Start connection to the backend with the given ip and port
     *
     * @param ip the ip address to connect to
     * @param port the port to open
     */
    private fun startConnection(ip: String, port: Int) {
        this.socket = Socket(ip, port)
        out = PrintWriter(socket.getOutputStream(), true)
        inp = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    /**
     * Method for sending a message
     *
     * @param msg the message to send
     * @return the reply
     */
    private fun sendMessage(msg: String): String {
        out.println(msg)

        val temp = inp.readLine()
        println(temp)
        return temp
    }

    /**
     * Stop the connection
     *
     */
    private fun stopConnection() {
        try {
            inp.close()
            out.close()
            //this.socket.shutdownInput()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        catch (e: UninitializedPropertyAccessException){
            //e.printStackTrace()
            println("No connection.....")
        }

    }

    /**
     * Init method for initializing the rtcm switch status once
     *
     */
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
            if (jsonFromMessage.content == "RTCM NAK") {
                GnssDataUpdater.ThreadUtil.runOnUiThread {
                    rtcmSwitch.isChecked = !rtcmSwitch.isChecked
                }

            }
            stopConnection()
            rtcmExecutor.shutdown()
        }
        isInstantiated = true
    }

    /**
     * Show progress dialog when configuration is loading
     * no user input is allowed until ready
     *
     * @param dTitle the title to display
     * @param dText the text to display
     */
    private fun showProgressDialog(dTitle:String, dText:String){
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val pbPadding = 50
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(pbPadding, llPadding, pbPadding, llPadding)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER

        val tvText = TextView(context)
        tvText.text = dText
        tvText.setTextColor(parseColor("#000000"))
        tvText.textSize = 14f
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)
        builder.setTitle(dTitle)

        dialog = builder.create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    /**
     * Dismiss the dialog
     */
    private fun dismissDialog() {
        dialog.dismiss()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}