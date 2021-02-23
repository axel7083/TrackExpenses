package com.github.trackexpenses.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.trackexpenses.DatabaseHelper
import com.github.trackexpenses.IItems
import com.github.trackexpenses.R
import com.github.trackexpenses.adapters.ExpenseViewHolder
import com.github.trackexpenses.adapters.MultipleViewAdapter
import com.github.trackexpenses.models.*
import com.github.trackexpenses.utils.ActivityResultUtils
import com.github.trackexpenses.utils.TimeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*


class HistoryActivity : AppCompatActivity(), ExpenseViewHolder.ExpenseClickListener {


    private lateinit var adapter: MultipleViewAdapter
    private lateinit var items: ArrayList<IItems>
    private lateinit var categories: ArrayList<Category>
    private lateinit var settings: Settings

    private lateinit var monday: Calendar
    private lateinit var sunday: Calendar

    // SQLite Database
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        db = DatabaseHelper(this)

        val week: Week = Gson().fromJson(intent.getStringExtra("week"), Week::class.java)

        categories = Gson().fromJson(
            intent.getStringExtra("categories"),
            object : TypeToken<List<Category?>?>() {}.type
        )

        settings = Gson().fromJson(
            intent.getStringExtra("settings"),
            Settings::class.java
        )

        monday = TimeUtils.toCalendar(week.date, TimeUtils.SIMPLE_PATTERN)
        monday[Calendar.HOUR] = 0
        monday[Calendar.MINUTE] = 0
        monday[Calendar.SECOND] = 0
        monday[Calendar.MILLISECOND] = 0

        sunday = TimeUtils.getSunday(week.date, "Europe/Paris", TimeUtils.SIMPLE_PATTERN)

        Log.d(
            TAG,
            "Monday " + monday.time.toString() + " sunday " + sunday.time.toString()
        )

        loadItem()

        adapter =
            MultipleViewAdapter(items, categories, settings.currency, this)
        history_rv.adapter = adapter
        history_rv.layoutManager = LinearLayoutManager(this)

        back_history.setOnClickListener { onBackPressed() }
    }

    private fun loadItem() {
        items = TimeUtils.separateWithTitle(db.getExpenses(monday, sunday), true)
    }

    private fun refresh() {
        adapter.items = (items)
        adapter.notifyDataSetChanged()
    }

    var modified: Boolean = false
    override fun onBackPressed() {

        Log.d(TAG, "onBackPressed")
        if(modified) {
            setResult(RESULT_OK, null)
        }
        else
        {
            setResult(RESULT_CANCELED, null)
        }
        finish()
    }

    override fun onItemClick(view: View?, position: Int) {
        Log.d(TAG, "Clicked " + (items[position] as Expense).Title)
        val intent = Intent(this, ExpenseActivity::class.java)
        intent.putExtra("categories", Gson().toJson(categories))
        intent.putExtra("expense", Gson().toJson(items[position]))
        intent.putExtra("settings", Gson().toJson(settings))
        startActivityForResult(intent, MainActivity.EXPENSE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $resultCode")

        if(requestCode == MainActivity.EXPENSE_ACTIVITY) {
            if (resultCode == RESULT_CANCELED || data == null)
                return
            else
            {
                if( ActivityResultUtils.handleExpenseActivityResult(data, db, null, settings) ) {
                    categories = db.categories
                }
                // Refreshing
                loadItem()
                refresh()
                modified = true

            }
        }
    }


    companion object {
        const val TAG = "HistoryActivity"
    }
}