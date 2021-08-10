package com.example.gnssnavigationstatus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.contentValuesOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gnssnavigationstatus.service.GnssDataUpdater
import com.example.gnssnavigationstatus.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * The Main activity of this app
 *
 * This activity is responsible for creating the navigation bar,
 * the fragments and for storing and loading shared preferences
 */
class MainActivity : AppCompatActivity() {

    /**
     * Companion
     * some static variables that are needed in the fragments
     */
    companion object{
        lateinit var IP: String
        var isChecked: Boolean? = null
        var isConnected: Boolean = false
        lateinit var instance:MainActivity
    }


    /**
     * This is called on creating the activity
     * Perform initialization of all fragments.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = this

        loadPreferences()

        if(isChecked == true) {
            val frag = SettingsFragment()
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

    /**
     * This is called on destroying this
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        IP.let {
            this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                ?.edit()?.putString("ip", it)?.apply()
        }
    }

    /**
     * Load stored values from shared preferences
     *
     */
    private fun loadPreferences(){
        IP = this.getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getString("ip", "").toString()
        println("Getting last stored ip address: $IP")

        isChecked = this.getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getBoolean("switch_state", false)
    }



}
