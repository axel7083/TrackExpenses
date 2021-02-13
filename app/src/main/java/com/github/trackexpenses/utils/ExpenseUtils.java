package com.github.trackexpenses.utils;

import android.util.Log;
import android.util.Pair;

import com.github.trackexpenses.DatabaseHelper;
import com.github.trackexpenses.activities.MainActivity;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Settings;
import com.github.trackexpenses.models.Week;

import java.util.ArrayList;

import static com.github.trackexpenses.utils.WeekUtils.getWeekLeft;

public class ExpenseUtils {

    public static Pair<Double,Double> computeWeekAllowance(ArrayList<Week> weeks, Settings settings) {
        double remaining = getRemaining(weeks, settings);
        long weekLeft = getWeekLeft(settings.endFormatted);
        weekLeft--;

        return new Pair<>(remaining, (double) remaining/weekLeft);
    }

    public static double getRemaining(ArrayList<Week> weeks, Settings settings) {
        double value = 0;

        for(Week w : weeks) {
            value+=w.spent;
        }
        return settings.amount-value;
    }
}
