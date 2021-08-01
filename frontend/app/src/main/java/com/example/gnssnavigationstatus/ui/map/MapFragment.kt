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
import com.example.gnssnavigationstatus.service.GnssDataUpdater
import com.example.gnssnavigationstatus.ui.map.map_components.DrawFragment
import org.w3c.dom.Text
import java.util.concurrent.Executors

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var drawFragment: DrawFragment

    companion object {
        lateinit var timeTextView: TextView
        lateinit var longitudeTextView: TextView
        lateinit var latitudeTextView: TextView
        lateinit var heightTextView: TextView
        lateinit var verticalAccuracyTextView: TextView
        lateinit var horizontalAccuracyTextView: TextView
        lateinit var numberSatsTextView: TextView
        lateinit var gnssFixOkTextView: TextView
        lateinit var connectionStatus:TextView
        lateinit var rtcmStatus:TextView
        lateinit var refStation:TextView
        lateinit var fixType:TextView
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
        heightTextView = root.findViewById(R.id.height_text)
        verticalAccuracyTextView = root.findViewById(R.id.vert_acc_text)
        horizontalAccuracyTextView = root.findViewById(R.id.horiz_acc_text)
        numberSatsTextView = root.findViewById(R.id.number_sats_text)
        gnssFixOkTextView = root.findViewById(R.id.gnss_fix_ok_text)
        connectionStatus = root.findViewById(R.id.connection_status)
        fixType = root.findViewById(R.id.fix_type_text)
        rtcmStatus = root.findViewById(R.id.rtcm_used_text)
        refStation = root.findViewById(R.id.ref_station_text)

        return root
    }
}