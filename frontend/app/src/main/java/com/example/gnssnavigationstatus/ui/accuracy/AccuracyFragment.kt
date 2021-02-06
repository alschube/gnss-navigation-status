package com.example.gnssnavigationstatus.ui.accuracy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gnssnavigationstatus.R

class AccuracyFragment : Fragment() {

    private lateinit var accuracyViewModel: AccuracyViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        accuracyViewModel =
                ViewModelProvider(this).get(AccuracyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_accuracy, container, false)
        //val textView: TextView = root.findViewById(R.id.text_notifications)
        accuracyViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }
}