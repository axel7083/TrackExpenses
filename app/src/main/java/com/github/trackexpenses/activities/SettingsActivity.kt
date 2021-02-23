package com.github.trackexpenses.activities

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.trackexpenses.R
import com.github.trackexpenses.databinding.ActivityMainBinding
import com.github.trackexpenses.databinding.ActivitySettingsBinding
import com.github.trackexpenses.fragments.OverviewSettingsFragment
import com.github.trackexpenses.fragments.SettingsFragment
import com.github.trackexpenses.models.OverviewSettings
import com.github.trackexpenses.models.Settings
import com.google.gson.Gson


class SettingsActivity : AppCompatActivity(), OverviewSettingsFragment.OverviewCallback {

    private lateinit var binding: ActivitySettingsBinding


    private lateinit var settings: Settings
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var overviewSettingsFragment: OverviewSettingsFragment

    private var isDefault: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        settings = Gson().fromJson(intent.getStringExtra("settings"), Settings::class.java)

        settingsFragment = SettingsFragment(settings)
        overviewSettingsFragment = OverviewSettingsFragment(settings.overviewSettings, this)

        displayDefaultSettings()

        binding.backSettings.setOnClickListener {
            onBackPressed()
        }
    }

    private fun displayDefaultSettings() {
        isDefault = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, settingsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun displayOverviewSettings() {
        isDefault = false
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, overviewSettingsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun resetApplication() {
        Log.d(TAG, "resetApplication")
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    }

    override fun onBackPressed() {
        //If we are in a subcategory of settings we display main
        if(!isDefault)
            displayDefaultSettings()
        else
        {
            val returnIntent = Intent()
            returnIntent.putExtra("settings", Gson().toJson(settingsFragment.settings))
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onChange(overviewSettings: OverviewSettings?) {
        if(overviewSettings != null)
            settings.overviewSettings = overviewSettings
    }

    companion object {
        const val TAG: String = "SettingsActivity"
    }

}