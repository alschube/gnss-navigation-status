package com.example.gnssnavigationstatus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gnssnavigationstatus.service.GnssDataUpdater
import com.example.gnssnavigationstatus.ui.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var IP: String
        var isChecked: Boolean? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IP = this.getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getString("ip", "").toString()

        isChecked = this.getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getBoolean("switch_state", false)

        if(isChecked == true) {
            var frag = SettingsFragment()
            frag.init()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_sat_map, R.id.navigation_sat_table, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        Intent(this, GnssDataUpdater::class.java).also { intent ->
            startService(intent)
        }
    }
    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}