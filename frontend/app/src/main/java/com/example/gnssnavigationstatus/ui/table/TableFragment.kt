package com.example.gnssnavigationstatus.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.GnssDataHolder
import com.example.gnssnavigationstatus.data.SatelliteData
import com.example.gnssnavigationstatus.service.SatelliteAdapter


class TableFragment : Fragment() {

    private lateinit var tableViewModel: TableViewModel

    companion object {
        lateinit var listView: RecyclerView
        var dataList: MutableLiveData<List<SatelliteData>> = MutableLiveData()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        tableViewModel =
                ViewModelProvider(this).get(TableViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_table, container, false)

        //listView.setHasFixedSize(false)
        //print(GnssData.satellites)
        val adapter = GnssDataHolder.satellites?.let { SatelliteAdapter(dataList) }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)

        listView = root.findViewById(R.id.satellite_table_view)
        listView.layoutManager = layoutManager
        listView.adapter = adapter

        return root
    }

    fun dummies(): List<SatelliteData> {
        val dummyList: ArrayList<SatelliteData> = ArrayList()
        val sat1 = SatelliteData()
        val sat2 = SatelliteData()
        val sat3 = SatelliteData()

        sat1.svId = 1
        sat2.svId = 2
        sat3.svId = 3

        sat1.type = "GPS"
        sat2.type = "Galileo"
        sat3.type = "Glonass"

        sat1.azimut = 270
        sat2.azimut = 180
        sat3.azimut = 90

        sat1.elevation = 90
        sat2.elevation = 45
        sat3.elevation = 30

        sat1.signalStrength = 1
        sat2.signalStrength = 2
        sat3.signalStrength = 3

        dummyList.add(sat1)
        dummyList.add(sat2)
        dummyList.add(sat3)

        return dummyList
    }
}