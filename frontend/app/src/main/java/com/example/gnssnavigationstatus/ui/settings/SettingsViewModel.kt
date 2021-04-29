package com.example.gnssnavigationstatus.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _isChecked = MutableLiveData<Boolean>().apply {

    }
    val boolean: LiveData<Boolean> = _isChecked

}