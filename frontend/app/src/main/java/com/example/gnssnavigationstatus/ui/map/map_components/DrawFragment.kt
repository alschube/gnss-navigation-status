package com.example.gnssnavigationstatus.ui.map.map_components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.gnssnavigationstatus.R

/**
 * Draw fragment that holds the map
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class DrawFragment : Fragment() {

    private lateinit var constraintLayout: ConstraintLayout

    companion object {
        lateinit var map: Map
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

        val root = inflater.inflate(R.layout.fragment_draw, container, false)
        this.constraintLayout = root.findViewById(R.id.map_layout)
        map = Map(root.context, root.width, root.height)
        this.constraintLayout.addView(map)

        return root
    }
}