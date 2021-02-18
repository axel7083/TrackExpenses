package com.github.trackexpenses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.trackexpenses.R
import com.github.trackexpenses.fragments.SettingsFragment
import com.github.trackexpenses.models.Settings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    private lateinit var settings: Settings
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings = Gson().fromJson(intent.getStringExtra("settings"),Settings::class.java)

        settingsFragment = SettingsFragment(settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, settingsFragment)
            .commit()

        Toast.makeText(this,"WORK IN PROGRESS",Toast.LENGTH_SHORT).show()

        back_settings.setOnClickListener {
            finishActivity()
        }
    }

    private fun finishActivity() {

        val returnIntent = Intent()
        returnIntent.putExtra("settings", Gson().toJson(settingsFragment.settings))
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onBackPressed() {
        finishActivity()
    }
}