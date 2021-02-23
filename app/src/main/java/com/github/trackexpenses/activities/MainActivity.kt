package com.github.trackexpenses.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.trackexpenses.DatabaseHelper
import com.github.trackexpenses.R
import com.github.trackexpenses.databinding.ActivityHistoryBinding
import com.github.trackexpenses.databinding.ActivityMainBinding
import com.github.trackexpenses.fragments.HomeFragment
import com.github.trackexpenses.fragments.StatsFragment
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.models.Week
import com.github.trackexpenses.utils.ActivityResultUtils
import com.github.trackexpenses.utils.ExpenseUtils
import com.github.trackexpenses.utils.TimeUtils
import com.github.trackexpenses.utils.TimeUtils.isEnded
import com.github.trackexpenses.utils.TimeUtils.isStarted
import com.github.trackexpenses.utils.WeekUtils
import com.github.trackexpenses.utils.WeekUtils.*
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    // SQLite Database
    lateinit var db: DatabaseHelper

    // Share preference data
    lateinit var settings: Settings

    // Data extracted from DB
    lateinit var weeks: ArrayList<Week>
    lateinit var allowance: Pair<Double, Double>

    // Important data
    lateinit var currency: String

    private lateinit var homeFragment: HomeFragment
    private lateinit var statsFragment: StatsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        if(db.categories.size == 0)
            db.loadDefault(this)

        if(!fetchData()) {
            // If no data found => first launch
            val intent = Intent(this, IntroActivity::class.java)
            startActivityForResult(intent, INTRO_ACTIVITY)
            return
        }

        checkPeriod(true)
    }

    private fun checkPeriod(shouldSetup: Boolean) {
        when {
            isEnded(settings) -> {
                hideAlertDialog()
                // If now out of time => finish purpose
                showAlertDialog("Done",
                    getString(R.string.end_alert_content) ,
                    getString(R.string.dismiss))
                binding.cvMainAdd.visibility = View.GONE
                binding.bottomBar.visibility = View.GONE
                if(shouldSetup)
                    setup(true)
            }
            !isStarted(settings) -> {
                showAlertDialog("Not started yet.",
                    getString(R.string.start_alert_content) + TimeUtils.formatTitle(TimeUtils.toCalendar(settings.startFormatted).toInstant(),"Europe/Paris",true),
                    getString(R.string.dismiss))
                if(shouldSetup)
                    setup(false)

                binding.cvMainAdd.visibility = View.GONE
                binding.bottomBar.visibility = View.GONE
            }
            else -> {
                hideAlertDialog()
                if(shouldSetup)
                    setup(false)
                binding.cvMainAdd.visibility = View.VISIBLE
                binding.bottomBar.visibility = View.VISIBLE
            }
        }
    }

    private fun setup(isStat: Boolean) {
        getDataFromDB(true)
        setupViews()
        createFragments()
        switchFragment(isStat)
    }

    private fun getDataFromDB(refreshWeeks: Boolean) {
        if(refreshWeeks)
            weeks = db.weeks
            allowance = ExpenseUtils.computeNextWeekAllowance(
            weeks,
            settings
        )

        if(allowance.second == Double.MIN_VALUE) {
            Log.d(TAG,"Currently last week or after.")
        }

    }

    private fun fetchData(): Boolean {
        val mPrefs = getSharedPreferences("Default", MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("Settings", null) ?: return false

        settings = gson.fromJson(json, Settings::class.java)
        currency = settings.currency
        return true
    }

    private fun saveData() {
        val mPrefs = getSharedPreferences("Default", MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(settings)
        prefsEditor.putString("Settings", json)
        prefsEditor.apply()
    }

    private fun setupViews() {
        Log.d(TAG, "setupViews")
        binding.home.setOnClickListener(this)
        binding.stats.setOnClickListener(this)
        binding.plusMain.setOnClickListener(this)
        binding.settingsBtn.setOnClickListener(this)
    }

    private fun createFragments() {
        Log.d(TAG, "createFragments")
        homeFragment = HomeFragment.newInstance(0)
        statsFragment = StatsFragment.newInstance(0)
    }

    private var isStats = true
    fun switchFragment(isStats: Boolean) {

        this.isStats = isStats

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment: Fragment
        if (this.isStats) {
            fragment = statsFragment
            binding.home.setTextColor(getColor(R.color.grey))
            binding.stats.setTextColor(getColor(R.color.blue))
        } else {
            fragment = homeFragment
            binding.home.setTextColor(getColor(R.color.blue))
            binding.stats.setTextColor(getColor(R.color.grey))
        }
        fragmentTransaction.replace(R.id.contentFrame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onClick(p0: View?) {
        if(p0 == null)
            return

        Log.d(TAG, "onClick " + p0.id + " | " + R.id.home)

        when(p0.id) {
            R.id.home -> switchFragment(false)
            R.id.stats -> switchFragment(true)
            R.id.plus_main -> openExpense()
            R.id.settings_btn -> openSettings()
            R.id.alertAction -> {
                //TODO: Multiple action ?
                hideAlertDialog()
            }
            else -> Log.d(TAG, "Something unknown clicked (" + p0.id + ")")
        }
    }

    private fun showAlertDialog(title: String, content: String, action: String) {
        binding.alert.visibility = View.VISIBLE
        binding.alertTitle.text = title
        binding.alertContent.text = content
        binding.alertAction.text = action
        binding.alertAction.setOnClickListener(this)
    }

    private fun hideAlertDialog() {
        binding.alert.visibility = View.GONE
    }

    private fun openSettings() {
        val i1 = Intent(this, SettingsActivity::class.java)
        i1.putExtra("settings", Gson().toJson(settings))
        startActivityForResult(i1, SETTINGS_ACTIVITY)
    }

    private fun openExpense() {
        val i1 = Intent(this, ExpenseActivity::class.java)
        i1.putExtra("categories", Gson().toJson(db.categories))
        i1.putExtra("settings", Gson().toJson(settings))
        startActivityForResult(i1, EXPENSE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $requestCode RESULT_CANCELED: $RESULT_CANCELED RESULT_OK: $RESULT_OK resultCode: $resultCode")
        when(requestCode) {
            EXPENSE_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED || data == null)
                    return
                ActivityResultUtils.handleExpenseActivityResult(data, db, weeks, settings)
                // Refreshing
                getDataFromDB(true)
                homeFragment.refresh()
                statsFragment.refresh()
            }
            INTRO_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED || data == null) {
                    finish()
                    return
                }
                handleIntroActivityResult(data)
            }
            SETTINGS_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED || data == null) {
                    return
                }
                handleSettingActivityResult(data)
            }
            HISTORY_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED) {
                    return
                }
                handleHistoryActivityResult()
            }
            else -> {
                Log.d(TAG, "Unknown activity requestCode")
            }
        }
    }

    private fun handleSettingActivityResult(data: Intent) {

        val settings = Gson().fromJson(data.getStringExtra("settings"),Settings::class.java)


        if(!settings.equals(this.settings)){
            Toast.makeText(this,"Setting has changed. Updating..",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"settings: " + settings.currency)


            if(!this.settings.compareEndDate(settings)) {
                Log.d(TAG,"End date edited")

                val weeks2 = WeekUtils.createWeeks(settings)
                val weeks1 = db.allWeeks

                //Do we have to update the database ?
                if(weeks2.size != weeks1.size) {
                    if(weeks2.size > weeks1.size) {
                        Log.d(TAG,"The new number of week is greater than the old one (${weeks1.size} | ${weeks2.size})")
                        val missingWeeks = getMissingWeeks(weeks1, weeks2)
                        Log.d(TAG,"Adding ${missingWeeks.size} weeks to db")
                        for(week in missingWeeks) {
                            Log.d(TAG, "week: $week")
                            db.addWeek(week)
                        }
                    }
                    else
                    {
                        Log.d(TAG,"The new number of week is smaller than the old one")
                        val removedWeeks = getRemovedWeeks(weeks1,weeks2)
                        Log.d(TAG,"Removing ${removedWeeks.size} weeks to db")
                        for(week in removedWeeks) {
                            Log.d(TAG, "week: $week")
                            db.deleteWeek(week.ID)
                        }
                    }

                    weeks = updateCurrentWeek(settings, true, db)
                }
                else
                {
                    Log.d(TAG,"New end date on same week than previous, no change to db.")
                }
            }

            if(!this.settings.compareAmount(settings)) {
                Log.d(TAG,"Amount updated")
                weeks = updateCurrentWeek(settings, true, db)
            }

            if(!this.settings.compareCurrency(settings)) {
                currency = settings.currency
            }

            this.settings = settings
            saveData()

            checkPeriod(false)
            getDataFromDB(true)
            homeFragment.refresh()
            statsFragment.refresh()
        }

    }

    private fun handleIntroActivityResult(data: Intent) {

        settings = Gson().fromJson(data.getStringExtra("settings"), Settings::class.java)
        Log.d(TAG, settings.toString())
        checkPeriod(true)

        //creating all weeks to database
        val weeks = WeekUtils.createWeeks(settings)
        if (weeks != null) {
            Log.d(TAG, "getMissingWeeks COUNT: " + weeks.size)
            for (w in weeks)
                db.addWeek(w)
        }

        currency = settings.currency
        saveData()
    }

    private fun handleHistoryActivityResult() {

        Log.d(TAG,"handleHistoryActivityResult")

        getDataFromDB(true)
        homeFragment.refresh()
        statsFragment.refresh()
    }

    var backCount: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() / 1000 - backCount < 3) {
            finish()
        } else {
            backCount = System.currentTimeMillis() / 1000
            Toast.makeText(this, R.string.back_alert, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val EXPENSE_ACTIVITY: Int = 1
        const val INTRO_ACTIVITY: Int = 2
        const val SETTINGS_ACTIVITY: Int = 3
        const val HISTORY_ACTIVITY: Int = 4
        const val TAG: String = "MainActivity"
    }
}