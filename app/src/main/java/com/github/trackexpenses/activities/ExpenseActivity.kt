package com.github.trackexpenses.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.trackexpenses.R
import com.github.trackexpenses.databinding.ActivityExpenseBinding
import com.github.trackexpenses.dialogs.CategoriesDialog
import com.github.trackexpenses.models.Category
import com.github.trackexpenses.models.Expense
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.utils.CategoryUtils
import com.github.trackexpenses.utils.TimeUtils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.ZoneId
import java.util.*


class ExpenseActivity : AppCompatActivity(), View.OnClickListener, CategoriesDialog.Callback {

    private lateinit var binding: ActivityExpenseBinding

    private lateinit var categories: ArrayList<Category>

    private lateinit var category: Category
    private lateinit var expense: Expense
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        categories = Gson().fromJson(
            intent.getStringExtra("categories"),
            object : TypeToken<List<Category?>?>() {}.type
        )

        settings = Gson().fromJson(
            intent.getStringExtra("settings"), Settings::class.java
        )

        binding.currencyName.text = when(settings.currency) {
            getString(R.string.euro_sign) -> "Euro"
            getString(R.string.dollar_sign) -> "Dollar"
            getString(R.string.mark_sign) -> "Mark"
            else -> "None"
        }

        val expJson = intent.getStringExtra("expense")
        if(expJson != null) {
            //Restore states
            binding.deleteLayout.visibility = View.VISIBLE

            binding.titleActivityExpense.text = getString(R.string.expense_edit)
            expense = Gson().fromJson(expJson, Expense::class.java)
            Log.d(TAG, "Editing expense " + expense.ID + " category ID: " + expense.Category)
            binding.expenseTitle.setText(expense.Title)
            binding.expenseValue.setText(expense.Value.toString())

            category = CategoryUtils.getCategory(expense.Category.toString(), categories)
            binding.dateDisplay.text = TimeUtils.formatTitle(
                TimeUtils.toCalendar(expense.Date).toInstant(), "Europe/Paris", true
            )
        }
        else
        {
            binding.deleteLayout.visibility = View.GONE
            binding.titleActivityExpense.text = getString(R.string.expense_title)
            //Default one
            category = categories[0]
            expense = Expense()
            expense.Category = category.ID.toLong()
        }


        setupViews()

    }

    private fun setupViews() {
        //Navigation button
        binding.btnCancel.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        binding.calendarInput.setOnClickListener(this)
        binding.backExpense.setOnClickListener(this)
        binding.deleteLayout.setOnClickListener(this)

        //Setup category
        binding.categoryInput.setOnClickListener(this)
        binding.categoryDisplay.text = category.name
        binding.categoryDisplaySmiley.setText(category.smiley)

        binding.delete.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 == null)
            return;

        when (p0.id) {
            R.id.calendar_input -> showCalendarDialog()
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

    private fun showCalendarDialog() {

        val monday =TimeUtils.getMonday(
            settings.startFormatted,
            "Europe/Paris"
        );
        val now = TimeUtils.getNow()
        now[Calendar.HOUR_OF_DAY] = 23
        now[Calendar.MINUTE] = 59
        now[Calendar.SECOND] = 59

        val builderRange = MaterialDatePicker.Builder.datePicker()

        if(expense.Date != null )
            builderRange.setSelection(TimeUtils.toCalendar(expense.Date).timeInMillis)
        else
            builderRange.setSelection(now.timeInMillis)

        val constraintsBuilderRange = CalendarConstraints.Builder()

        constraintsBuilderRange.setValidator(object : DateValidator {
            override fun isValid(date: Long): Boolean {
                val pick = Calendar.getInstance()
                pick.timeInMillis = date

                return !pick.before(monday) && !pick.after(now)
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {}
        })

        builderRange.setTheme(R.style.ThemeOverlayCatalogMaterialCalendarCustom)
        builderRange.setTitleText("Choose a date")

        builderRange.setCalendarConstraints(constraintsBuilderRange.build())

        val pickerRange = builderRange.build()

        pickerRange.addOnCancelListener { }
        pickerRange.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.timeInMillis = pickerRange.selection!!

            expense.Date = (TimeUtils.formatSQL(cal.toInstant(), "Europe/Paris"))
            binding.dateDisplay.text = TimeUtils.formatTitle(cal.toInstant(), "Europe/Paris", true)
        }

        pickerRange.show(supportFragmentManager,"0")
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
        expense.Title = binding.expenseTitle.text.toString()
        if(expense.Title.length < 3) {
            Toast.makeText(this, "Label too short", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check value
        try {
            expense.Value = binding.expenseValue.text.toString().toDouble()
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
        if(binding.categoryDisplaySmiley.text.length > 2) {

            Log.d(TAG, "display_smiley: " + binding.categoryDisplaySmiley.text.length)

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
                category.smiley = binding.categoryDisplaySmiley.text.toString()
                returnIntent.putExtra("category", Gson().toJson(category))
            }
            else //If the category already exist
            {
                Log.d(TAG, "Current catagory ID: " + category.ID)
                Log.d(TAG, "Current smiley SAVED: " + category.smiley)

                //Maybe the smiley of the category change
                Log.d(
                    TAG,
                    "ORIGINAL: " + category.smiley + " current " + binding.categoryDisplaySmiley.text
                )
                if(!category.smiley.equals(binding.categoryDisplaySmiley.text.toString()))
                {
                    Log.d(TAG, "Smiley updated")
                    category.smiley = binding.categoryDisplaySmiley.text.toString()
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
     * Callback from CategoriesDialog
     * **/
    override fun addCategory(category: Category) {
        Log.d(TAG, "addCategory")
        binding.categoryDisplay.text = category.name
        binding.categoryDisplaySmiley.setText("+")
        binding.categoryDisplaySmiley.focusable = View.FOCUSABLE

        this.category = (category)
    }

    override fun selectCategory(ID: String) {
        Log.d(TAG, "selectCategory")

        for(i in 0 until categories.size) {
            if(categories[i].ID.equals(ID)) {
                category = categories[i]
                break
            }
        }

        binding.categoryDisplay.text = category.name
        binding.categoryDisplaySmiley.setText(category.smiley)

        expense.Category = category.ID.toLong()
    }

    companion object {
        private const val TAG: String = "ExpenseActivity"
    }
}