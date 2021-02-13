package com.github.trackexpenses.utils;

import android.util.Log;


import com.github.trackexpenses.DatabaseHelper;
import com.github.trackexpenses.activities.MainActivity;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Settings;
import com.github.trackexpenses.models.Week;

import java.util.ArrayList;

import kotlin.Pair;

import static com.github.trackexpenses.utils.WeekUtils.getWeekLeft;

public class ExpenseUtils {

    private static final String TAG = "ExpenseUtils";
    /**
     * @param weeks All weeks
     * @param settings App settings
     * @return Pair remaining - weekly allowance
     */
    public static Pair<Double,Double> computeNextWeekAllowance(ArrayList<Week> weeks, Settings settings) {
        double remaining = getRemaining(weeks, settings);
        long weekLeft = getWeekLeft(settings.endFormatted);
        weekLeft--; //Remove current week

        double allowance = 0;
        Log.d(TAG,"weekLeft: " + weekLeft);

        if(weekLeft == 0)
            return new Pair<>(remaining, remaining);
        else if(weekLeft < 0) // The period of time is FINISHED.
            return new Pair<>(remaining, Double.MIN_VALUE);
        else
            return new Pair<>(remaining, (double) remaining/weekLeft);

    }

    public static double computeNowWeekAllowance(ArrayList<Week> weeks, Settings settings) {
        double remaining = getRemaining(weeks, settings);
        long weekLeft = getWeekLeft(settings.endFormatted);

        Log.d(TAG,"weekLeft: " + weekLeft);

        if(weekLeft != 0)
            return (double) remaining/weekLeft;
        else
            return remaining;
    }

    public static double getRemaining(ArrayList<Week> weeks, Settings settings) {
        double value = 0;

        for(Week w : weeks) {
            value+=w.spent;
        }
        return settings.amount-value;
    }
}
