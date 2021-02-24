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
import com.github.trackexpenses.databinding.ActivityExpenseBinding
import com.github.trackexpenses.databinding.ActivityHistoryBinding
import com.github.trackexpenses.models.*
import com.github.trackexpenses.utils.ActivityResultUtils
import com.github.trackexpenses.utils.TimeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class HistoryActivity : AppCompatActivity(), ExpenseViewHolder.ExpenseClickListener {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: MultipleViewAdapter
    private var items: ArrayList<IItems>? = null
    private lateinit var categories: ArrayList<Category>
    private lateinit var settings: Settings

    private lateinit var monday: Calendar
    private lateinit var sunday: Calendar

    // SQLite Database
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.historyRv.adapter = adapter
        binding.historyRv.layoutManager = LinearLayoutManager(this)

        binding.backHistory.setOnClickListener { onBackPressed() }
    }

    private fun loadItem() {
        items = TimeUtils.separateWithTitle(db.getExpenses(monday, sunday), true)

        if(items == null) {
            onBackPressed()
        }
    }

    private fun refresh() {
        adapter.categories = categories
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
        Log.d(TAG, "Clicked " + (items!![position] as Expense).Title)
        val intent = Intent(this, ExpenseActivity::class.java)
        intent.putExtra("categories", Gson().toJson(categories))
        intent.putExtra("expense", Gson().toJson(items!![position]))
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
                modified = true

                // Refreshing
                loadItem()
                refresh()

            }
        }
    }


    companion object {
        const val TAG = "HistoryActivity"
    }
}