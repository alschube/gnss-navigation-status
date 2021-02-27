package com.example.gnssnavigationstatus.service

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.RecyclerView
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.SatelliteData

class SatelliteAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private var liveData: MutableLiveData<List<SatelliteData>>) : RecyclerView.Adapter<SatelliteAdapter.MyViewHolder>() {
    companion object {
        lateinit var satelliteList: List<SatelliteData>
        fun reInit(list: List<SatelliteData>): List<SatelliteData> {
            for (sat: SatelliteData in list){
                sat.type = sat.determineType(sat.gnssId)
                sat.satelliteIdentifier = sat.createSatelliteIdentifier(sat.svId, sat.gnssId)
            }
            return list
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(v: ViewGroup) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case

        var idTV: TextView
        var iconIV: ImageView
        var typeTV: TextView
        var azimTV: TextView
        var elevTV: TextView
        var signalStrengthTV: TextView

        init {
            idTV = v.findViewById(R.id.id_tv)
            iconIV = v.findViewById(R.id.icon_iv)
            typeTV = v.findViewById(R.id.sat_type_tv)
            azimTV = v.findViewById(R.id.azimut_tv)
            elevTV = v.findViewById(R.id.elevation_tv)
            signalStrengthTV = v.findViewById(R.id.signal_strength_tv)
        }
    }

    init {
        //this.liveData = MutableLiveData()
        for (sat: SatelliteData in satelliteList){
            sat.type = sat.determineType(sat.gnssId)
            sat.satelliteIdentifier = sat.createSatelliteIdentifier(sat.svId, sat.gnssId)
        }
        this.liveData.postValue(satelliteList)
        this.liveData.observeForever {
            this.notifyDataSetChanged()::class
        }
    }

    fun update() {
        this.liveData.postValue(satelliteList)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_satellite, parent, false) as ViewGroup
        return MyViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.idTV.text = liveData.value?.get(position)?.satelliteIdentifier
        holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_circle_24px))
        holder.typeTV.text = liveData.value?.get(position)?.type.toString()
        holder.azimTV.text = liveData.value?.get(position)?.azimut.toString()
        holder.elevTV.text = liveData.value?.get(position)?.elevation.toString()
        holder.signalStrengthTV.text = liveData.value?.get(position)?.signalStrength.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (this.liveData.value == null) 0 else this.liveData.value?.size!!
    }
}