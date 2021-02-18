package com.github.trackexpenses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.trackexpenses.R
import com.github.trackexpenses.fragments.SettingsFragment
import com.github.trackexpenses.models.Settings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)



        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment(Gson().fromJson(intent.getStringExtra("settings"),Settings::class.java)))
            .commit()

        Toast.makeText(this,"WORK IN PROGRESS",Toast.LENGTH_SHORT).show()

        back_settings.setOnClickListener {
            finishActivity(false)
        }
    }

    private fun finishActivity(save: Boolean) {

        val returnIntent = Intent()

        if(!save)
            setResult(RESULT_CANCELED, returnIntent)
        else
        {
            //returnIntent.putExtra()
            setResult(RESULT_OK, returnIntent)
        }
        finish()
    }
}