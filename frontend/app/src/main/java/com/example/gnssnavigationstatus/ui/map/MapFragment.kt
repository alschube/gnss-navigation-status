package com.example.gnssnavigationstatus.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.GnssDataHolder
import org.w3c.dom.Text

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    companion object {
        lateinit var timeTextView: TextView
        lateinit var longitudeTextView: TextView
        lateinit var latitudeTextView: TextView
        //lateinit var gnssFixOKTextView: TextView
        lateinit var heightTextView: TextView
        lateinit var verticalAccuracyTextView: TextView
        lateinit var horizontalAccuracyTextView: TextView
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
                ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        timeTextView = root.findViewById(R.id.time_text)
        longitudeTextView = root.findViewById(R.id.long_text)
        latitudeTextView = root.findViewById(R.id.lat_text)
        // gnssFixOKTextView = root.findViewById(R.id.gnss_fix_ok_text)
        heightTextView = root.findViewById(R.id.height_text)
        verticalAccuracyTextView = root.findViewById(R.id.vert_acc_text)
        horizontalAccuracyTextView = root.findViewById(R.id.horiz_acc_text)

        /*activity?.runOnUiThread(object : Runnable {
            override fun run() {

                    if (GnssDataHolder.time != null) {
                        timeTextView.text = GnssDataHolder.time
                        longitudeTextView.text = "${GnssDataHolder.longitude}"
                        latitudeTextView.text = "${GnssDataHolder.latitude}"
                        //gnssFixOKTextView.text = "${data.gnssFixOK}"
                        heightTextView.text = "${GnssDataHolder.height?.div(1000)}"
                        verticalAccuracyTextView.text =
                            "${GnssDataHolder.verticalAccuracy?.div(10)}"
                        horizontalAccuracyTextView.text =
                            "${GnssDataHolder.horizontalAccuracy?.div(10)}"
                    }

            }
        }
        )*/

        return root
    }
}