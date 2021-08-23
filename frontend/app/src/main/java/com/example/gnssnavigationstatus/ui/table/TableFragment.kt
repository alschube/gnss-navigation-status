package com.example.gnssnavigationstatus.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.GnssDataHolder
import com.example.gnssnavigationstatus.data.SatelliteData
import com.example.gnssnavigationstatus.service.SatelliteAdapter

/**
 * Table fragment
 *
 * The second fragment
 * This displays the received satellites in a table
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class TableFragment : Fragment() {

    private lateinit var tableViewModel: TableViewModel

    companion object {
        lateinit var listView: RecyclerView
        var dataList: MutableLiveData<List<SatelliteData>> = MutableLiveData()
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
        tableViewModel =
                ViewModelProvider(this).get(TableViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_table, container, false)

        if(GnssDataHolder.satellites == null){
            Toast.makeText(context, "Keine Satellitendaten vorhanden, überpüfe die Verbindung!", Toast.LENGTH_SHORT).show()
        }
        val adapter = GnssDataHolder.satellites?.let { SatelliteAdapter(dataList) }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)

        listView = root.findViewById(R.id.satellite_table_view)
        listView.layoutManager = layoutManager
        listView.adapter = adapter

        return root
    }
}