package com.github.trackexpenses.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.trackexpenses.ISettingChanged;
import com.github.trackexpenses.R;
import com.github.trackexpenses.dialogs.DatePickerPreference;
import com.github.trackexpenses.dialogs.DatePickerPreferenceDialogFragment;
import com.github.trackexpenses.models.Settings;

public class SettingsFragment  extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    // name for sharedPreferences location
    private static final String TAG = "SettingsFragment";
    private static final String SHARED_PREFERENCES = "testandroidxpreferences";
    private static final String DIALOG_FRAGMENT_TAG =
            "androidx.preference.PreferenceFragment.DIALOG";

    private Settings settings;

    public SettingsFragment(Settings settings) {
        this.settings = settings;
    }




    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        /* Set the amount preference */
        EditTextPreference editTextPreference = getPreferenceManager().findPreference("amount");
        if (editTextPreference != null) {
            bindPreferenceSummaryToValue(editTextPreference);
            editTextPreference.setSummary(settings.getAmount() + settings.getCurrency());
            editTextPreference.setDefaultValue(settings.getAmount());

            editTextPreference.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setSelection( 0,editText.getText().length() );
            });

            editTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                double value = 0;
                try {
                    value = Double.parseDouble((String) newValue);
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Invalid value.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                return true;
            });
        }

        /* end_date */
        Preference end_date = findPreference("end_date");
        if(end_date != null) {
            end_date.setSummary(settings.getEndFormatted());
            end_date.setDefaultValue(settings.getEndFormatted());
            end_date.setOnPreferenceChangeListener(this);
        }


        /* currency list */



        Preference autoCompletePreference = findPreference("country");
        if (autoCompletePreference != null) {
            autoCompletePreference.setSelectable(true);
        }
        Preference numberPickerPreference = findPreference("number_picker_preference");
        if (numberPickerPreference != null) {
            bindPreferenceSummaryToValue(numberPickerPreference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Log.d(TAG,"onPreferenceChange");
        if (preference instanceof DatePickerPreference) {
            String chosenNumber = newValue.toString();
            savePreferences(preference.getKey(), chosenNumber);
            preference.setSummary(chosenNumber);
            if (newValue.toString().isEmpty()) {
                String defaultValue = ((DatePickerPreference) preference).getDefaultValue();
                preference.setSummary(defaultValue);
                ((DatePickerPreference) preference).setValue(defaultValue);
            }
            else {
                preference.setSummary(chosenNumber);
                ((DatePickerPreference) preference).setDefaultValue(chosenNumber);
                ((DatePickerPreference) preference).setValue(chosenNumber);
            }

        }



        /* else if (preference instanceof TextAutoCompletePreference) {
            String chosenCountry = newValue.toString();
            savePreferences(preference.getKey(), chosenCountry);
            if (newValue.toString().isEmpty()) {
                String defaultValue = ((TextAutoCompletePreference) preference).getDefaultValue();
                preference.setSummary(defaultValue);
                ((TextAutoCompletePreference) preference).setValue(defaultValue);
            }
            else {
                preference.setSummary(chosenCountry);
                ((TextAutoCompletePreference) preference).setValue(chosenCountry);
            }
        }*/
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        /*if (preference instanceof TextAutoCompletePreference) { // for the name
            String preferenceString = restorePreferences(preference.getKey());
            if ((preferenceString == null || preferenceString.isEmpty())) {
                // when there is no saved data - put the default value
                onPreferenceChange(preference, ((TextAutoCompletePreference) preference).getDefaultValue());
            } else {
                onPreferenceChange(preference, preferenceString);
            }
        }
        else*/ if (preference instanceof DatePickerPreference) {
            String preferenceString = restorePreferences(preference.getKey());
            if ((preferenceString == null ||preferenceString.isEmpty())) {
                // when there is no saved data - put the default value
                onPreferenceChange(preference, ((DatePickerPreference) preference).getDefaultValue());
            } else {
                onPreferenceChange(preference, preferenceString);
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // check if dialog is already showing
        if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        final DialogFragment f;

        if (preference instanceof DatePickerPreference) {
            f = DatePickerPreferenceDialogFragment.newInstance(preference.getKey(),settings.endFormatted);

        } /*else if (preference instanceof TextAutoCompletePreference) {
            f = TextAutoCompletePreferenceDialogFragment.newInstance(preference.getKey());
        } */else
            f = null;

        if (f != null) {
            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    // This method to store the custom preferences changes
    private void savePreferences(String key, String value) {

        Log.d(TAG,"savePreferences (" + key + " - " + value + ")");

        if(key.equals(getString(R.string.end_date_key))) {
            settings.setEndFormatted(value);
        }
        else if(key.equals(getString(R.string.currency_key)))
        {
            settings.setCurrency(value);
        }
        else if(key.equals(getString(R.string.amount_key)))
        {
            settings.setAmount(Double.parseDouble(value));
        }
    }

    // This method to restore the custom preferences data
    private String restorePreferences(String key) {
        if(key.equals(getString(R.string.end_date_key))) {
            return settings.getEndFormatted();
        }
        else if(key.equals(getString(R.string.currency_key)))
        {
            return settings.getCurrency();
        }
        else if(key.equals(getString(R.string.amount_key)))
        {
            return settings.getEndFormatted();
        }
        return null;
    }


}
