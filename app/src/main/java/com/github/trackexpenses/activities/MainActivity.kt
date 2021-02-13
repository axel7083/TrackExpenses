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
import com.github.trackexpenses.fragments.HomeFragment
import com.github.trackexpenses.fragments.StatsFragment
import com.github.trackexpenses.models.Category
import com.github.trackexpenses.models.Expense
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.models.Week
import com.github.trackexpenses.utils.ExpenseUtils
import com.github.trackexpenses.utils.ExpenseUtils.computeNowWeekAllowance
import com.github.trackexpenses.utils.TimeUtils
import com.github.trackexpenses.utils.TimeUtils.isEnded
import com.github.trackexpenses.utils.TimeUtils.isStarted
import com.github.trackexpenses.utils.WeekUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


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
        setContentView(R.layout.activity_main)
        db = DatabaseHelper(this)
        if(db.categories.size == 0)
            db.loadDefault(this)

        if(!fetchData()) {
            // If no data found => first launch
            val intent = Intent(this, IntroActivity::class.java)
            startActivityForResult(intent, INTRO_ACTIVITY)
            return
        }
        else
        {
            val period = settings!!.startFormatted + " -> " + settings!!.endFormatted;
            Log.d(TAG, "PERIOD: $period")
            Toast.makeText(this, period, Toast.LENGTH_LONG).show()

           // showAlertDialog("Debugging", "Debug purpose only", "Debug")
           //val weeks = db.weeks
        }




        Log.d(TAG, "DEBUG")
        val ws = db.allWeeks
        for(w in ws) {
            Log.d(TAG, "${w.ID} - ${w.date} - ${w.goal}")
        }

        Log.d(TAG, "onCreate")


        when {
            isEnded(settings) -> {
                // If now out of time => finish purpose
                showAlertDialog("Done",
                    getString(R.string.end_alert_content) ,
                    getString(R.string.dismiss))
                cv_main_add.visibility = View.GONE
                bottom_bar.visibility = View.GONE
                setup(true)
            }
            !isStarted(settings) -> {
                showAlertDialog("Not started yet.",
                    getString(R.string.start_alert_content) + TimeUtils.formatTitle(TimeUtils.toCalendar(settings.startFormatted).toInstant(),"Europe/Paris",true),
                    getString(R.string.dismiss))
                cv_main_add.visibility = View.GONE
                bottom_bar.visibility = View.GONE
            }
            else -> {
                setup(false)
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
        return settings != null
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
        home.setOnClickListener(this)
        stats.setOnClickListener(this)
        plus_main.setOnClickListener(this)
        settings_btn.setOnClickListener(this)
    }

    private fun createFragments() {
        Log.d(TAG, "createFragments")
        homeFragment = HomeFragment.newInstance(0)
        statsFragment = StatsFragment.newInstance(0)
    }

    private var isStats = true
    private fun switchFragment(isStats: Boolean) {

        this.isStats = isStats

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment: Fragment
        if (this.isStats) {
            fragment = statsFragment
            home.setTextColor(getColor(R.color.grey))
            stats.setTextColor(getColor(R.color.blue))
        } else {
            fragment = homeFragment
            home.setTextColor(getColor(R.color.blue))
            stats.setTextColor(getColor(R.color.grey))
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
        alert.visibility = View.VISIBLE
        alertTitle.text = title
        alertContent.text = content
        alertAction.text = action
        alertAction.setOnClickListener(this)
    }

    private fun hideAlertDialog() {
        alert.visibility = View.GONE
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
        Log.d(TAG, "onActivityResult")
        when(requestCode) {
            EXPENSE_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED || data == null)
                    return
                handleExpenseActivityResult(data)
            }
            INTRO_ACTIVITY -> {
                if (resultCode == RESULT_CANCELED || data == null) {
                    finish()
                    return
                }
                handleIntroActivityResult(data)
            }
            else -> {
                Log.d(TAG, "Unknown activity requestCode")
            }
        }
    }

    private fun handleIntroActivityResult(data: Intent) {

        settings = Gson().fromJson(data.getStringExtra("settings"), Settings::class.java)
        if(settings != null) {
            Log.d(TAG, settings.toString())
            setup(false)

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
        else
        {
            Toast.makeText(this, "Error not settings received", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun handleExpenseActivityResult(data: Intent) {
        Log.d(TAG, "handleExpenseActivityResult")

        if(data.getBooleanExtra("delete", false)) {
            val ID = data.getStringExtra("ID")

            Log.d(TAG, "Deleting expense $ID")
            db.deleteExpense(ID)
        }
        else
        {
            val catJson = data.getStringExtra("category")
            val expense: Expense = Gson().fromJson(
                data.getStringExtra("expense"),
                Expense::class.java
            )
            Log.d(TAG, "New expense called ${expense.Title}")

            if(catJson != null) {
                Log.d(TAG, "New category added")
                val newCat = Gson().fromJson(catJson, Category::class.java)

                if(newCat.ID == null) {
                    val id = db.addCategory(newCat)
                    Log.d(TAG, "Saved in data base with id $id")
                    expense.Category = id
                }
                else
                {
                    db.updateCategory(newCat)
                    Log.d(TAG, "Category updated")
                    expense.Category = newCat.ID.toLong()
                }
            }
            else
            {
                Log.d(TAG, "Category ID: ${expense.Category}")
            }

            if(expense.ID == null) {

                //If nothing set for the current week
                if(WeekUtils.getNow(weeks) == null) {
                    weeks = db.weeks
                    Log.d(TAG, "First expense of the week. Setup goal:")
                    val currentMonday = TimeUtils.formatSimple(
                        TimeUtils.getFirstDayOfWeek("Europe/Paris").toInstant(), "Europe/Paris"
                    )
                    val nowAllowance = computeNowWeekAllowance(weeks, settings)
                    Log.d(TAG, "currentMonday: $currentMonday nowAllowance: $nowAllowance")

                    db.updateWeek(currentMonday, nowAllowance)
                }

                db.addExpense(expense)
            }
            else
            {
                db.updateExpense(expense)
            }
        }
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
        const val TAG: String = "MainActivity"
    }
}