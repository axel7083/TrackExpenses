package com.github.trackexpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.github.trackexpenses.models.Category;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Week;
import com.github.trackexpenses.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    //database name
    public static final String DATABASE_NAME = "11zon";
    //database version
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_EXPENSE = "tbl_expense";
    public static final String TABLE_CATEGORY = "tbl_category";
    public static final String TABLE_WEEK = "tbl_week";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        //creating table
        query = "CREATE TABLE " + TABLE_EXPENSE + "(ID INTEGER PRIMARY KEY, Title TEXT, Category INTEGER, Value REAL, Date TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_CATEGORY + "(ID INTEGER PRIMARY KEY, Name TEXT, Smiley TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_WEEK + "(ID INTEGER PRIMARY KEY, Goal REAL, Date TEXT)";
        db.execSQL(query);
    }

    public void loadDefault(Context context) {
        Log.d(TAG,"Load default");
        String[] names = context.getResources().getStringArray(R.array.categories_name);
        String[] smileys = context.getResources().getStringArray(R.array.categories_smiley);

        for(int i = 0 ; i < names.length; i++)
        {
            addCategory(new Category(null, names[i], smileys[i]));
        }
    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEK);
        onCreate(db);
    }

    //add the new expense
    public long addExpense(Expense expense) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_EXPENSE, null , extractValuesExpense(expense));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }

    public long addWeek(Week week) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_WEEK, null , extractValuesWeek(week));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }


    //add the new category
    public long addCategory(Category category) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_CATEGORY, null , extractValuesCategory(category));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }

    //get the all expenses
    public ArrayList<Expense> getExpenses() {
        // select all query
        String select_query= "SELECT *FROM " + TABLE_EXPENSE;
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        return extractExpenses(cursor);
    }

    //get the all expenses
    public ArrayList<Category> getCategories() {
        // select all query
        String select_query= "SELECT *FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        return extractCategories(cursor);
    }

    public double getTotalMoneySpent() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor c = db.rawQuery("select sum(Value) from " + TABLE_EXPENSE, null);
        double value = 0;
        if(c.moveToFirst())
            value = c.getDouble(0);
        else
            value = -1;
        c.close();
        return value;
    }

    /**
     * @return the total amount spent per week
     */
    /*public ArrayList<Pair<String,Double>> getSpentPerWeek() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT DATE(Date,'weekday 0', '-6 days') AS this_monday, SUM(value) AS value FROM " + TABLE_EXPENSE + " GROUP BY this_monday;", null);

        ArrayList<Pair<String,Double>> arrayList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pair<String, Double> week = new Pair<>(cursor.getString(0),cursor.getDouble(1));
                arrayList.add(week);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }*/


    /**
     * @return the total amount spent per category
     */
    public ArrayList<Pair<Long,Double>> getSpentPerCategory() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT category, SUM(value) as total FROM " + TABLE_EXPENSE + " GROUP BY category", null);

        ArrayList<Pair<Long,Double>> arrayList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pair<Long, Double> cat = new Pair<>(cursor.getLong(0),cursor.getDouble(1));
                arrayList.add(cat);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<Week> getWeeks() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + TABLE_WEEK + ".ID," + TABLE_WEEK + ".goal,"+ TABLE_WEEK +".Date,SUM(" + TABLE_EXPENSE + ".Value) FROM " + TABLE_WEEK + " INNER JOIN "+ TABLE_EXPENSE +" on Date(" + TABLE_EXPENSE + ".Date,'weekday 0','-6 days') = " + TABLE_WEEK + ".Date GROUP BY " + TABLE_WEEK + ".ID ORDER BY " + TABLE_WEEK + ".Date DESC;", null);

        ArrayList<Week> arrayList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null)
                    arrayList.add(new Week(cursor.getDouble(1), cursor.getString(2), cursor.getDouble(3) ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    public double getMoneySpent(Calendar start, Calendar end) {
        SQLiteDatabase db = this .getWritableDatabase();

        String startDateQueryDate = TimeUtils.formatSQL(start.toInstant(),"Europe/Paris");
        String endDateQueryDate = TimeUtils.formatSQL(end.toInstant(),"Europe/Paris");

        Cursor c = db.rawQuery("select sum(Value) from " + TABLE_EXPENSE + " where Date BETWEEN '" + startDateQueryDate + "' AND '" + endDateQueryDate + "'", null);

        double value = 0;
        if(c.moveToFirst())
            value = c.getDouble(0);
        else
            value = -1;
        c.close();
        return value;
    }

    //get all expenses between dates
    public ArrayList<Expense> getExpenses(Calendar start, Calendar end) {

        String startDateQueryDate = TimeUtils.formatSQL(start.toInstant(),"Europe/Paris");
        String endDateQueryDate = TimeUtils.formatSQL(end.toInstant(),"Europe/Paris");

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_EXPENSE + " where Date BETWEEN '" + startDateQueryDate + "' AND '" + endDateQueryDate + "' ORDER BY Date DESC", null);

        return extractExpenses(cursor);
    }

    //get all expenses during the current week
    public ArrayList<Expense> getCurrentWeekExpenses() {
        return getExpenses(
                TimeUtils.getFirstDayOfWeek("Europe/Paris"),
                TimeUtils.getLastDayOfWeek("Europe/Paris")
        );
    }

    //delete an expense
    public void deleteExpense(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_EXPENSE, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    //delete a category
    public void deleteCategory(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_CATEGORY, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    //update the expense
    public void updateExpense(Expense expense) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        sqLiteDatabase.update(TABLE_EXPENSE, extractValuesExpense(expense), "ID=" + expense.getID(), null);
        sqLiteDatabase.close();
    }

    //update the category
    public void updateCategory(Category category) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        sqLiteDatabase.update(TABLE_CATEGORY, extractValuesCategory(category), "ID=" + category.getID(), null);
        sqLiteDatabase.close();
    }


    private static ArrayList<Expense> extractExpenses(Cursor cursor) {
        ArrayList<Expense> arrayList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(cursor.getString(0));
                expense.setTitle(cursor.getString(1));
                expense.setCategory(cursor.getLong(2));
                expense.setValue(cursor.getDouble(3));
                expense.setDate(cursor.getString(4));

                arrayList.add(expense);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    private static ArrayList<Category> extractCategories(Cursor cursor) {
        ArrayList<Category> arrayList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setID(cursor.getString(0));
                category.setName(cursor.getString(1));
                category.setSmiley(cursor.getString(2));

                arrayList.add(category);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    private static ContentValues extractValuesExpense(Expense expense) {
        ContentValues values =  new ContentValues();
        values.put("Title", expense.getTitle());
        values.put("Category", expense.getCategory());
        values.put("Value", expense.getValue());
        values.put("Date", expense.getDate());
        return values;
    }

    private static ContentValues extractValuesWeek(Week week) {
        Log.d(TAG,"extractValuesWeek: " + week.toString());
        ContentValues values =  new ContentValues();
        values.put("Date", week.date);
        values.put("Goal", week.goal);
        return values;
    }

    private static ContentValues extractValuesCategory(Category category) {
        ContentValues values =  new ContentValues();
        values.put("Name", category.getName());
        values.put("Smiley", category.getSmiley());
        return values;
    }
}
