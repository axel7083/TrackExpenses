package com.github.trackexpenses.utils

import android.content.Intent
import android.util.Log
import androidx.annotation.Nullable
import com.github.trackexpenses.DatabaseHelper
import com.github.trackexpenses.activities.MainActivity
import com.github.trackexpenses.models.Category
import com.github.trackexpenses.models.Expense
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.models.Week
import com.github.trackexpenses.utils.WeekUtils.updateCurrentWeek
import com.google.gson.Gson

class ActivityResultUtils {
    companion object {

        fun handleExpenseActivityResult(data: Intent, db: DatabaseHelper, @Nullable weeks: ArrayList<Week>?, settings: Settings): Boolean {
            Log.d(MainActivity.TAG, "handleExpenseActivityResult")
            var categoryAdded = false

            if(data.getBooleanExtra("delete", false)) {
                val ID = data.getStringExtra("ID")

                Log.d(MainActivity.TAG, "Deleting expense $ID")
                db.deleteExpense(ID)
            }
            else
            {
                val catJson = data.getStringExtra("category")
                val expense: Expense = Gson().fromJson(
                    data.getStringExtra("expense"),
                    Expense::class.java
                )
                Log.d(MainActivity.TAG, "New expense called ${expense.Title}")

                if(catJson != null) {
                    Log.d(MainActivity.TAG, "New category added")
                    categoryAdded = true
                    val newCat = Gson().fromJson(catJson, Category::class.java)

                    if(newCat.ID == null) {
                        val id = db.addCategory(newCat)
                        Log.d(MainActivity.TAG, "Saved in data base with id $id")
                        expense.Category = id
                    }
                    else
                    {
                        db.updateCategory(newCat)
                        Log.d(MainActivity.TAG, "Category updated")
                        expense.Category = newCat.ID.toLong()
                    }
                }
                else
                {
                    Log.d(MainActivity.TAG, "Category ID: ${expense.Category}")
                }

                if(expense.ID == null) {

                    //If nothing set for the current week
                    if(WeekUtils.getNow(weeks) == null) {
                        Log.d(MainActivity.TAG, "First expense of the week. Setup goal:")
                        updateCurrentWeek(settings, false, db)
                    }

                    db.addExpense(expense)

                    // If the user add an expense to a previous week
                    if(TimeUtils.toCalendar(expense.Date).before(TimeUtils.getFirstDayOfWeek("Europe/Paris"))) {
                        Log.d(MainActivity.TAG,"Expense from previous week added to db, recomputing current week")
                        updateCurrentWeek(settings, true, db)
                    }
                }
                else
                {
                    db.updateExpense(expense)
                }
            }
            return categoryAdded
        }
    }
}