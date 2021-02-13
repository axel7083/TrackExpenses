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

    /**
     * @param weeks All weeks
     * @param settings App settings
     * @return Pair remaining - weekly allowance
     */
    public static Pair<Double,Double> computeNextWeekAllowance(ArrayList<Week> weeks, Settings settings) {
        double remaining = getRemaining(weeks, settings);
        long weekLeft = getWeekLeft(settings.endFormatted);
        weekLeft--; //Remove current week

        return new Pair<>(remaining, (double) remaining/weekLeft);
    }

    public static double computeNowWeekAllowance(ArrayList<Week> weeks, Settings settings) {
        double remaining = getRemaining(weeks, settings);
        long weekLeft = getWeekLeft(settings.endFormatted);

        return (double) remaining/weekLeft;
    }

    public static double getRemaining(ArrayList<Week> weeks, Settings settings) {
        double value = 0;

        for(Week w : weeks) {
            value+=w.spent;
        }
        return settings.amount-value;
    }
}
