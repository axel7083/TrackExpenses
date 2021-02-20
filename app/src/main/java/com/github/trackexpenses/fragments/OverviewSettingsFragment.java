package com.github.trackexpenses.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.github.trackexpenses.R;
import com.github.trackexpenses.models.OverviewSettings;

import lombok.Getter;

public class OverviewSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    // name for sharedPreferences location
    private static final String TAG = "OverviewSettingsFragment";

    private int[] keys = new int[] {
            (R.string.remaining_key),
            (R.string.allowance_key),
            (R.string.spent_key),
            (R.string.period_key),
            (R.string.top_category_key),
    };

    private OverviewCallback overviewCallback;
    public OverviewSettings overviewSettings;

    public OverviewSettingsFragment(OverviewSettings overviewSettings, OverviewCallback overviewCallback) {
        this.overviewCallback = overviewCallback;

        if(overviewSettings == null) {
            overviewSettings = new OverviewSettings();
        }
        this.overviewSettings = overviewSettings;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.overview_preferences, rootKey);

        for(int key : keys) {
            SwitchPreferenceCompat pref = getPreferenceManager().findPreference(getString(key));
            if(pref != null) {
                pref.setOnPreferenceChangeListener(this);
                pref.setChecked(restorePreferences(getString(key)));
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG,"onPreferenceChange");

        savePreferences(preference.getKey(), newValue);
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        Log.d(TAG,"bindPreferenceSummaryToValue");
        preference.setOnPreferenceChangeListener(this);
    }


    // This method to store the custom preferences changes
    private void savePreferences(String key, Object value) {

        Log.d(TAG,"savePreferences (" + key + " - " + value + ")");
        boolean newValue = (boolean) value;

        if(key.equals(getString(R.string.remaining_key))) {
            overviewSettings.setDisplayRemaining(newValue);
        }
        else if(key.equals(getString(R.string.allowance_key)))
        {
            overviewSettings.setDisplayNextWeekAllowance(newValue);
        }
        else if(key.equals(getString(R.string.spent_key)))
        {
            overviewSettings.setDisplaySpent(newValue);
        }
        else if(key.equals(getString(R.string.period_key)))
        {
            overviewSettings.setDisplayPeriod(newValue);
        }
        else if(key.equals(getString(R.string.top_category_key)))
        {
            overviewSettings.setDisplayTopCategory(newValue);
        }

        overviewCallback.onChange(overviewSettings);
    }

    // This method to restore the custom preferences data
    private boolean restorePreferences(String key) {

        if(key.equals(getString(R.string.remaining_key))) {
            return overviewSettings.isDisplayRemaining();
        }
        else if(key.equals(getString(R.string.allowance_key)))
        {
            return overviewSettings.isDisplayNextWeekAllowance();
        }
        else if(key.equals(getString(R.string.spent_key)))
        {
            return overviewSettings.isDisplaySpent();
        }
        else if(key.equals(getString(R.string.period_key)))
        {
            return overviewSettings.isDisplayPeriod();
        }
        else if(key.equals(getString(R.string.top_category_key)))
        {
            return overviewSettings.isDisplayTopCategory();
        }

        return false;
    }


    public interface OverviewCallback {
        void onChange(OverviewSettings overviewSettings);
    }

}
