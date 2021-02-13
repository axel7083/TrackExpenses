package com.github.trackexpenses.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.trackexpenses.R
import com.github.trackexpenses.dialogs.CategoriesDialog
import com.github.trackexpenses.models.Category
import com.github.trackexpenses.models.Expense
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.utils.CategoryUtils
import com.github.trackexpenses.utils.TimeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_expense.*
import java.time.Instant
import java.time.ZoneId
import java.util.*


class ExpenseActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener, CategoriesDialog.Callback {

    private val TAG: String = "ExpenseActivity"

    private lateinit var categories: ArrayList<Category>

    private lateinit var category: Category
    private lateinit var expense: Expense
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        categories = Gson().fromJson(
            intent.getStringExtra("categories"),
            object : TypeToken<List<Category?>?>() {}.type
        )

        settings = Gson().fromJson(
            intent.getStringExtra("settings"),Settings::class.java
        )

        val expJson = intent.getStringExtra("expense")
        if(expJson != null) {
            //Restore states
            delete_layout.visibility = View.VISIBLE

            title_activity_expense.text = getString(R.string.expense_edit)
            expense = Gson().fromJson(expJson, Expense::class.java)
            Log.d(TAG, "Editing expense " + expense.ID + " category ID: " + expense.Category)
            expense_title.setText(expense.Title)
            expense_value.setText(expense.Value.toString())

            category = CategoryUtils.getCategory(expense.Category.toString(), categories)
            date_display.text = TimeUtils.formatTitle(
                TimeUtils.toCalendar(expense.Date).toInstant(), "Europe/Paris", true
            )
        }
        else
        {
            delete_layout.visibility = View.GONE
            title_activity_expense.text = getString(R.string.expense_title)
            //Default one
            category = categories[0]
            expense = Expense()
            expense.Category = category.ID.toLong()
        }


        setupViews()

    }

    private fun setupViews() {
        //Navigation button
        btn_cancel.setOnClickListener(this)
        btn_save.setOnClickListener(this)

        calendar_input.setOnClickListener(this)
        back_expense.setOnClickListener(this)
        delete_layout.setOnClickListener(this)

        //Setup category
        category_input.setOnClickListener(this)
        category_display.text = category.name
        category_display_smiley.setText(category.smiley)

        delete.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 == null)
            return;

        when (p0.id) {
            R.id.calendar_input -> {

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                calendar[Calendar.HOUR] = 23
                calendar[Calendar.MINUTE] = 59
                calendar[Calendar.SECOND] = 59
                calendar[Calendar.MILLISECOND] = 999

                val now: Calendar = Calendar.getInstance()
                val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),  // Initial year selection
                    now.get(Calendar.MONTH),  // Initial month selection
                    now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                )
                dpd.maxDate = calendar
                dpd.minDate = TimeUtils.getMonday(settings.startFormatted,"Europe/Paris")
                // If you're calling this from a support Fragment
                // If you're calling this from a support Fragment
                dpd.show(supportFragmentManager, "Datepickerdialog");
            }
            R.id.btn_cancel, R.id.back_expense -> finishActivity(false)
            R.id.category_input -> showCategoriesDialog()
            R.id.btn_save -> {
                if (checkValues()) {
                    finishActivity(true)
                }
            }
            R.id.delete -> deleteExpense()
            else -> {

            }
        }
    }

    private fun showCategoriesDialog() {
        val cdd = CategoriesDialog(this, categories, this)
        val window: Window? = cdd.window
        if (window == null) {
            Log.d(TAG, "Erreur windows null")
            return
        }
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cdd.show()
        window.setGravity(Gravity.BOTTOM)
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun checkValues(): Boolean {

        // Check title
        expense.Title = expense_title.text.toString()
        if(expense.Title.length < 3) {
            Toast.makeText(this, "Label too short", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check value
        try {
            expense.Value = expense_value.text.toString().toDouble()
        }
        catch (e: NumberFormatException)
        {
            Toast.makeText(this, "Incorrect expense value.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(expense.Value == 0.0) {
            Toast.makeText(this, "Zero amount not allowed.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check category's smiley
        if(category_display_smiley.text.length > 2) {

            Log.d(TAG, "display_smiley: " + category_display_smiley.text.length)

            Toast.makeText(
                this,
                "Please provide a valid emoji for the category",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        //Check date
        if(expense.Date == null) {
            //Then set to today
            expense.Date = TimeUtils.formatSQL(
                Instant.now().atZone(ZoneId.of("Europe/Paris")).toInstant(), "Europe/Paris"
            )
        }

        Log.d(TAG, expense.toString())
        return true
    }

    private fun deleteExpense() {
        Log.d(TAG, "deleteExpense")
        val returnIntent = Intent()
        returnIntent.putExtra("delete", true)
        returnIntent.putExtra("ID", expense.ID)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun finishActivity(save: Boolean) {

        val returnIntent = Intent()

        if(!save)
            setResult(RESULT_CANCELED, returnIntent)
        else
        {
            // If the category selected is new we send it
            if(category.ID == null)
            {
                //Save the smiley used
                category.smiley = category_display_smiley.text.toString()
                returnIntent.putExtra("category", Gson().toJson(category))
            }
            else //If the category already exist
            {
                Log.d(TAG, "Current catagory ID: " + category.ID)
                Log.d(TAG, "Current smiley SAVED: " + category.smiley)

                //Maybe the smiley of the category change
                Log.d(
                    TAG,
                    "ORIGINAL: " + category.smiley + " current " + category_display_smiley.text
                )
                if(!category.smiley.equals(category_display_smiley.text.toString()))
                {
                    Log.d(TAG, "Smiley updated")
                    category.smiley = category_display_smiley.text.toString()
                    returnIntent.putExtra("category", Gson().toJson(category))
                }
            }
            returnIntent.putExtra("expense", Gson().toJson(expense))
            setResult(RESULT_OK, returnIntent)
        }

        finish()
    }

    override fun onBackPressed() {
        finishActivity(false)
    }

    /**
     * Callback from DatePicker
     * **/
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val output: Calendar = Calendar.getInstance()
        output[Calendar.YEAR] = year
        output[Calendar.DAY_OF_MONTH] = dayOfMonth
        output[Calendar.MONTH] = monthOfYear
        Log.d(TAG, "Calendar: " + output.time.toString())

        expense.Date = (TimeUtils.formatSQL(output.toInstant(), "Europe/Paris"))
        date_display.text = TimeUtils.formatTitle(output.toInstant(), "Europe/Paris", true)
    }

    /**
     * Callback from CategoriesDialog
     * **/
    override fun addCategory(category: Category) {
        Log.d(TAG, "addCategory")
        category_display.text = category.name
        category_display_smiley.setText("+")
        category_display_smiley.focusable = View.FOCUSABLE

        this.category = (category)
    }

    override fun selectCategory(position: Int) {
        Log.d(TAG, "selectCategory")
        category = categories[position]

        category_display.text = category.name
        category_display_smiley.setText(category.smiley)

        expense.Category = categories[position].ID.toLong()
    }

}