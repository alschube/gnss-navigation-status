package com.example.gnssnavigationstatus.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.R

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var checkBoxGPS: CheckBox
    private lateinit var checkBoxGAL: CheckBox
    private lateinit var checkBoxGLO: CheckBox
    private lateinit var checkBoxBDS: CheckBox

    private lateinit var checkBoxArray: Array<CheckBox>

    private lateinit var rtcmSwitch: Switch

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        //val textView: TextView = root.findViewById(R.id.text_notifications)
        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        this.checkBoxGPS = root.findViewById(R.id.checkBox_GPS)
        this.checkBoxGAL = root.findViewById(R.id.checkBox_GAL)
        this.checkBoxGLO = root.findViewById(R.id.checkBox_GLO)
        this.checkBoxBDS = root.findViewById(R.id.checkBox_BDS)

        this.checkBoxArray = arrayOf(checkBoxGPS, checkBoxGAL, checkBoxGLO, checkBoxBDS)

        this.rtcmSwitch = root.findViewById(R.id.rtcm_switch)

        return root
    }

    fun initCheckBoxesFromBackend(satelliteEnabledData: HashMap<String, Boolean>) {
        for (checkBox in checkBoxArray){
            checkBox.isChecked = satelliteEnabledData.get(checkBox.text.toString())!!
        }
    }
}