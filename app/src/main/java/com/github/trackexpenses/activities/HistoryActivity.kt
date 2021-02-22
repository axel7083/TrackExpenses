package com.github.trackexpenses.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.trackexpenses.IItems
import com.github.trackexpenses.R
import com.github.trackexpenses.adapters.ExpenseViewHolder
import com.github.trackexpenses.adapters.MultipleViewAdapter
import com.github.trackexpenses.fragments.HomeFragment
import com.github.trackexpenses.models.Category
import com.github.trackexpenses.models.Expense
import com.github.trackexpenses.models.ItemDeserializer
import com.github.trackexpenses.models.Settings
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*
import kotlin.collections.ArrayList


class HistoryActivity : AppCompatActivity(), ExpenseViewHolder.ExpenseClickListener {

    private lateinit var adapter: MultipleViewAdapter
    private lateinit var items: ArrayList<IItems>
    private lateinit var categories: ArrayList<Category>
    private lateinit var settings: Settings
    private lateinit var intents: ArrayList<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        intents = ArrayList()

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeHierarchyAdapter(IItems::class.java, ItemDeserializer())
        val gson = gsonBuilder.create()

        items = gson.fromJson(
            intent.getStringExtra("items"),
            object : TypeToken<List<IItems?>?>() {}.type
        )

        categories = Gson().fromJson(
            intent.getStringExtra("categories"),
            object : TypeToken<List<Category?>?>() {}.type
        )

        settings = Gson().fromJson(
            intent.getStringExtra("settings"),
            Settings::class.java
        )

        title_history.text = intent.getStringExtra("week")

        adapter =
            MultipleViewAdapter(items, categories, settings.currency, this)
        history_rv.adapter = adapter
        history_rv.layoutManager = LinearLayoutManager(this)

        back_history.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        Log.d(TAG,"onBackPressed")

        //TODO: if modification occur update
        val returnIntent = Intent()
        if(intents.size != 0) {
            returnIntent.putExtra("intents", Gson().toJson(intents))
            setResult(RESULT_OK, returnIntent)
        }
        else
        {
            setResult(RESULT_CANCELED, returnIntent)
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
                Log.d(TAG,"Adding intent")
                intents.add(data)
            }
        }
    }

    private fun extractAction(data: Intent) {
        if (data.getBooleanExtra("delete", false)) {

        }
    }


    companion object {
        const val TAG = "HistoryActivity"
    }
}