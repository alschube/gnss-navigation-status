package com.example.gnssnavigationstatus.ui.map.map_components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.service.GnssDataUpdater

class DrawFragment : Fragment() {

    lateinit var constraintLayout: ConstraintLayout

    companion object {
        lateinit var map: Map
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_draw, container, false)
        this.constraintLayout = root.findViewById(R.id.map_layout)
        map = Map(root.context, root.width, root.height)
        this.constraintLayout.addView(map)

        return root
    }
}