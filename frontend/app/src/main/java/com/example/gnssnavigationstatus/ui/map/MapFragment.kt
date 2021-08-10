package com.example.gnssnavigationstatus.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.R

/**
 * Map fragment
 *
 * The first fragment
 * This contains the drawFragment with its map and textviews for visualizing the
 * gnss and satellite data
 *
 */
class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    /** create some static textViews*/
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

        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        /** initialize the textViews*/
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