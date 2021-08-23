package com.example.gnssnavigationstatus.service

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.SatelliteData

/**
 * Satellite adapter class for managing the satellite recyclerView
 *
 * @property liveData the satellite list
 */
class SatelliteAdapter // Provide a suitable constructor
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

    /**
     * My view holder
     *
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     *
     *
     * @param v the viewGroup
     */
    class MyViewHolder(v: ViewGroup) : RecyclerView.ViewHolder(v) {

        /** initializing the textviews for the satellite data*/
        var idTV: TextView = v.findViewById(R.id.id_tv)
        var iconIV: ImageView = v.findViewById(R.id.icon_iv)
        var typeTV: TextView = v.findViewById(R.id.sat_type_tv)
        var azimTV: TextView = v.findViewById(R.id.azimut_tv)
        var elevTV: TextView = v.findViewById(R.id.elevation_tv)
        var signalStrengthTV: TextView = v.findViewById(R.id.signal_strength_tv)

    }

    /** init the list and update it*/
    init {
        for (sat: SatelliteData in satelliteList){
            sat.type = sat.determineType(sat.gnssId)
            sat.satelliteIdentifier = sat.createSatelliteIdentifier(sat.svId, sat.gnssId)
        }
        this.liveData.postValue(satelliteList)
        this.liveData.observeForever {
            this.notifyDataSetChanged()::class
        }
    }

    /**
     * Creates new views (invoked by the layout manager)
     *
     * @param parent the parent ViewGroup
     * @param viewType the type of this view
     * @return the created MyViewHolder object
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_satellite, parent, false) as ViewGroup
        return MyViewHolder(v)
    }

    //
    /**
     * Replace the contents of a view (invoked by the layout manager)
     *
     * @param holder the MyViewHolder object
     * @param position the positon which should be replaced
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.idTV.text = liveData.value?.get(position)?.satelliteIdentifier

        // check which type of satellite it is and set the icon according to it
        when (liveData.value?.get(position)?.gnssId){
            0 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_america_flag_icon))
            1 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_world_icon))
            2 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_europa_flag_icon))
            3 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_china_flag_icon))
            4 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_japan_flag_icon))
            5 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_japan_flag_icon))
            6 -> holder.iconIV.setImageIcon(Icon.createWithResource(holder.itemView.context, R.drawable.ic_russia_flag_icon))
        }

        holder.typeTV.text = liveData.value?.get(position)?.type.toString()
        holder.azimTV.text = liveData.value?.get(position)?.azimut.toString()
        holder.elevTV.text = liveData.value?.get(position)?.elevation.toString()
        holder.signalStrengthTV.text = liveData.value?.get(position)?.signalStrength.toString()
    }

    /**
     * Get item count of the dataset (invoked by the layout manager)
     *
     * @return
     */
    override fun getItemCount(): Int {
        return if (this.liveData.value == null) 0 else this.liveData.value?.size!!
    }
}