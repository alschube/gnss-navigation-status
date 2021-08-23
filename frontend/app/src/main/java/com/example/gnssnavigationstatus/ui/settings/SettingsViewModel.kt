package com.example.gnssnavigationstatus.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Settings view Model
 *
 * ViewModel is a class that is responsible for preparing and managing the data for
 * an {@link android.app.Activity Activity} or a {@link androidx.fragment.app.Fragment Fragment}.
 * It also handles the communication of the Activity / Fragment with the rest of the application
 * (e.g. calling the business logic classes).
 *
 * Developed by Aline Schubert in the period from January to August 2021.
 */
class SettingsViewModel : ViewModel() {

    private val _isChecked = MutableLiveData<Boolean>().apply {

    }
    val boolean: LiveData<Boolean> = _isChecked

}