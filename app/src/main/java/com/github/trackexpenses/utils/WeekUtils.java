package com.github.trackexpenses.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import com.github.trackexpenses.DatabaseHelper;
import com.github.trackexpenses.models.Settings;
import com.github.trackexpenses.models.Week;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;

import static com.github.trackexpenses.utils.ExpenseUtils.computeNowWeekAllowance;
import static com.github.trackexpenses.utils.TimeUtils.formatSimple;
import static com.github.trackexpenses.utils.TimeUtils.getFirstDayOfWeek;
import static com.github.trackexpenses.utils.TimeUtils.getMonday;
import static com.github.trackexpenses.utils.TimeUtils.toCalendar;

public class WeekUtils {

    private static final String TAG = "WeekUtils";

    public static ArrayList<Week> createWeeks(@Nullable Settings settings) {
        if(settings == null)
            return null;

        ArrayList<Week> weeks = new ArrayList<>();

        Calendar mondayStart = getMonday(settings.startFormatted,"Europe/Paris");
        Calendar mondayEnd = getMonday(settings.endFormatted,"Europe/Paris");

        boolean reachEnd = false;
        Calendar buffer = (Calendar) mondayStart.clone();

        if(mondayStart.getTime().toString().equals(mondayEnd.getTime().toString()))
        {
            Log.d(TAG,"mondayStart IS mondayEnd");
        }

        int weekCount = 0;
        while(!reachEnd) {
            Log.d(TAG,"+7 => " + buffer.getTime().toString());
            Instant instant = buffer.toInstant();
            if(buffer.getTime().toString().equals(mondayEnd.getTime().toString())) {
                reachEnd = true;
            }
            String str = formatSimple(instant,"Europe/Paris");
            Log.d(TAG,"missing => " + str);
            weeks.add(new Week(null, -1.0,str,-1.0));
            buffer.add(Calendar.DAY_OF_YEAR,7);
            weekCount++;

        }

        for(int i = 0 ; i < weeks.size(); i++)
        {
            weeks.get(i).setGoal(((double) settings.amount/weekCount));
        }

        Log.d(TAG,"Missing count: " + weeks.size());
        return weeks;
    }

    public static Week getNow(ArrayList<Week> weeks) {
        Calendar mondayNow = getFirstDayOfWeek("Europe/Paris");
        for(Week w : weeks) {
            if(w.getDate().equals(formatSimple(mondayNow.toInstant(),"Europe/Paris")))
                return w;
        }
        return null;
    }

    public static long getWeekLeft(String end) {
        long left = 0;

        Calendar mondayNow = getFirstDayOfWeek("Europe/Paris");
        Calendar mondayEnd = getMonday(end,"Europe/Paris");


        if(mondayEnd.before(mondayNow)) {
            Log.d(TAG,"getWeekLeft Time out");
            return -1;
        }

        if(mondayEnd.getTime().toString().equals(mondayNow.getTime().toString())){
            Log.d(TAG,"getWeekLeft equal");
            return 0;
        }

        boolean reachEnd = false;

        while(!reachEnd) {
            Log.d(TAG,"getWeekLeft +7 => " + mondayNow.getTime().toString());
            Instant instant = mondayNow.toInstant();
            if(mondayNow.getTime().toString().equals(mondayEnd.getTime().toString())) {
                reachEnd = true;
            }
            mondayNow.add(Calendar.DAY_OF_YEAR,7);
            left++;
        }

        return left;
    }

    public static ArrayList<Week> getMissingWeeks(ArrayList<Week> original, ArrayList<Week> newOne) {
        if(original.size()>newOne.size())
            return null;

        ArrayList<Week> weeks = new ArrayList<>();
        for(int i = original.size(); i < newOne.size(); i++) {
            weeks.add(newOne.get(i));
        }
        return weeks;
    }

    public static ArrayList<Week> getRemovedWeeks(ArrayList<Week> original, ArrayList<Week> newOne) {
        if(original.size()<=newOne.size())
            return null;

        ArrayList<Week> weeks = new ArrayList<>();
        for(int i = newOne.size(); i < original.size(); i++) {
            weeks.add(original.get(i));
        }
        return weeks;
    }


    public static ArrayList<Week> updateCurrentWeek(Settings settings, Boolean skipNow, DatabaseHelper db) {
        ArrayList<Week> weeks = db.getWeeks();
        String currentMonday = TimeUtils.formatSimple(
                TimeUtils.getFirstDayOfWeek("Europe/Paris").toInstant(), "Europe/Paris"
        );
        double nowAllowance = computeNowWeekAllowance(weeks, settings, skipNow);
        Log.d(TAG, "currentMonday: $currentMonday nowAllowance: $nowAllowance");

        db.updateWeek(currentMonday, nowAllowance);
        return weeks;
    }

    /*private fun updateCurrentWeek(settings: Settings, skipNow: Boolean) {
        weeks = db.weeks
        val currentMonday = TimeUtils.formatSimple(
                TimeUtils.getFirstDayOfWeek("Europe/Paris").toInstant(), "Europe/Paris"
        )
        val nowAllowance = computeNowWeekAllowance(weeks, settings, skipNow)
        Log.d(TAG, "currentMonday: $currentMonday nowAllowance: $nowAllowance")

        db.updateWeek(currentMonday, nowAllowance)
    }*/
}
