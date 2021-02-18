package com.github.trackexpenses.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.trackexpenses.ISettingChanged;
import com.github.trackexpenses.R;
import com.github.trackexpenses.dialogs.DatePickerPreference;
import com.github.trackexpenses.dialogs.VersionDialog;
import com.github.trackexpenses.models.Settings;
import com.github.trackexpenses.utils.TimeUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

import lombok.Getter;

public class SettingsFragment  extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    // name for sharedPreferences location
    private static final String TAG = "SettingsFragment";
    private static final String SHARED_PREFERENCES = "testandroidxpreferences";
    private static final String DIALOG_FRAGMENT_TAG =
            "androidx.preference.PreferenceFragment.DIALOG";

    @Getter
    public Settings settings;

    public SettingsFragment(Settings settings) {
        this.settings = settings;
    }




    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setupApplicationPreferences();

        setupGeneralPreferences();


    }

    private void setupApplicationPreferences() {
        /* Set the amount preference */
        EditTextPreference editTextPreference = getPreferenceManager().findPreference(getString(R.string.amount_key));
        if (editTextPreference != null) {
            bindPreferenceSummaryToValue(editTextPreference);

            editTextPreference.setSummary(settings.getAmount() + settings.getCurrency());
            editTextPreference.setDefaultValue(settings.getAmount());

            editTextPreference.setText(settings.getAmount() + "");

            editTextPreference.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setSelection( 0,editText.getText().length() );
            });

        }

        /* currency list */
        ListPreference currencyPreference = getPreferenceManager().findPreference(getString(R.string.currency_key));
        if(currencyPreference != null) {
            bindPreferenceSummaryToValue(currencyPreference);
            currencyPreference.setSummary(settings.getCurrency());

            CharSequence[] entries = currencyPreference.getEntryValues();
            for(int i = 0 ; i < entries.length ; i++) {
                if(settings.getCurrency().equals(entries[i])) {
                    currencyPreference.setValueIndex(i);
                }
            }

        }

        /* end_date */
        Preference end_date = findPreference(getString(R.string.end_date_key));
        if(end_date != null) {
            end_date.setSummary(settings.getEndFormatted());
            end_date.setDefaultValue(settings.getEndFormatted());
            bindPreferenceSummaryToValue(end_date);
        }

    }

    private void setupGeneralPreferences()  {
        Preference versionPreference = getPreferenceManager().findPreference(getString(R.string.version_key));
        if(versionPreference != null) {
            versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    VersionDialog dialog = new VersionDialog(getActivity());
                    Window window = dialog.getWindow();
                    if (window == null) {
                        Log.d(TAG, "Erreur windows null");
                        return false;
                    }

                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    window.setGravity(Gravity.BOTTOM);
                    window.setLayout(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    return false;
                }
            });
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG,"onPreferenceChange");
        boolean customSummary = false;


        if (preference instanceof DatePickerPreference) {
            customSummary = true;
            String chosenNumber = newValue.toString();
            preference.setSummary(chosenNumber);
            if (newValue.toString().isEmpty()) {
                String defaultValue = ((DatePickerPreference) preference).getDefaultValue();
                preference.setSummary(defaultValue);
                ((DatePickerPreference) preference).setValue(defaultValue);
            }
            else {
                preference.setSummary(TimeUtils.formatTitle(TimeUtils.toCalendar(chosenNumber).toInstant(),"Europe/Paris",true));
                ((DatePickerPreference) preference).setDefaultValue(chosenNumber);
                ((DatePickerPreference) preference).setValue(chosenNumber);
            }


        }

        if(preference.getKey().equals(getString(R.string.amount_key))) {
            double value = 0;
            try {
                value = Double.parseDouble((String) newValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid value.",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }



        savePreferences(preference.getKey(), (String) newValue);

        if(!customSummary)
            preference.setSummary((String) newValue);


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
        Log.d(TAG,"bindPreferenceSummaryToValue");
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
            Log.d(TAG,"DatePickerPreference");
            String preferenceString = restorePreferences(preference.getKey());
            if ((preferenceString == null ||preferenceString.isEmpty())) {
                // when there is no saved data - put the default value
                Log.d(TAG,"no saved data");
                onPreferenceChange(preference, ((DatePickerPreference) preference).getDefaultValue());
            } else {
                Log.d(TAG,"saved data");
                onPreferenceChange(preference, preferenceString);
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        Log.d(TAG,"onDisplayPreferenceDialog");

        // check if dialog is already showing
        if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }


        final DialogFragment f;

        if (preference instanceof DatePickerPreference) {
            //f = DatePickerPreferenceDialogFragment.newInstance(preference.getKey(),settings.endFormatted);

            f = createDatePickerDialog((DatePickerPreference) preference);

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

    private DialogFragment createDatePickerDialog(DatePickerPreference preference) {

        MaterialDatePicker.Builder<Long> builderRange =
                MaterialDatePicker.Builder.datePicker();
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        constraintsBuilderRange.setValidator(new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                Calendar pick = Calendar.getInstance();
                pick.setTimeInMillis(date);
                return !pick.before(TimeUtils.getNow());
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }
        });

        builderRange.setTheme(R.style.ThemeOverlayCatalogMaterialCalendarCustom);
        builderRange.setTitleText("Choose an end date");

        Log.d(TAG,"preference.getValue(): " + preference.getDefaultValue());
        if(preference.getValue() != null) {
                builderRange.setSelection(TimeUtils.toCalendar(preference.getValue()).getTimeInMillis());
        }


        builderRange.setCalendarConstraints(constraintsBuilderRange.build());

        MaterialDatePicker<Long> pickerRange = builderRange.build();

        pickerRange.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(pickerRange.getSelection());
                preference.callChangeListener(TimeUtils.formatSQL(cal.toInstant(),"Europe/Paris"));
            }
        });
        return pickerRange;
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
