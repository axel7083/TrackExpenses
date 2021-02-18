package com.github.trackexpenses.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.github.trackexpenses.R;
import com.github.trackexpenses.utils.TimeUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.sql.Time;
import java.util.Calendar;


public class DatePickerPreferenceDialogFragment extends PreferenceDialogFragmentCompat implements DialogInterface, MaterialPickerOnPositiveButtonClickListener<Long> {
    private static final String TAG = "DatePickerPreferenceDialogFragment";
    private static final String ARG_VAL = "ARG_VAL";
    private static final String SAVE_STATE_VALUE = "NumberPickerPreferenceDialogFragment.value";
    private static final String SHARED_PREFERENCES = "testandroidxpreferences"; // name for sharedPreferences location


    private String mValue;

    @NonNull
    public static DatePickerPreferenceDialogFragment newInstance(@NonNull String key, String default_val) {
        final DatePickerPreferenceDialogFragment fragment = new DatePickerPreferenceDialogFragment();
        final Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        args.putString(ARG_VAL, default_val);
        fragment.setArguments(args);
        return fragment;
    }

    MaterialDatePicker<Long> pickerRange;

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        //super.show(manager, tag);
       // mValue = getArguments().getString(ARG_VAL);
        mValue = getDatePickerPreference().getValue();
        Log.d(SHARED_PREFERENCES, "show " + mValue);

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


        if(mValue != null) {
            builderRange.setSelection(TimeUtils.toCalendar(mValue).getTimeInMillis());
        }


        /*if(mValue != null) {
            builderRange.setSelection(TimeUtils.toCalendar(mValue).getTimeInMillis());
        }
        else
        {
            builderRange.setSelection(TimeUtils.getNow().getTimeInMillis());
        }*/

        builderRange.setCalendarConstraints(constraintsBuilderRange.build());

        pickerRange = builderRange.build();
        pickerRange.addOnCancelListener(this);

        pickerRange.addOnPositiveButtonClickListener(this);

        pickerRange.show(manager, pickerRange.toString());
       // pickerRange.onDismiss(this);

    }


    // get the NumberPickerPreference instance
    private DatePickerPreference getDatePickerPreference() {
        return (DatePickerPreference) getPreference();
    }

    public DatePickerPreferenceDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(SHARED_PREFERENCES, "onCreateDialog");
        return pickerRange.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

        if(positiveResult) {
            Log.d(mValue,"onDialogClosed: " + mValue);
            getPreference().callChangeListener(mValue);
        }
    }

    @Override
    public void cancel() {
        Log.d(TAG,"cancel ");
        onDialogClosed(false);
    }

    @Override
    public void onPositiveButtonClick(Long selection) {
        Log.d(TAG,"onPositiveButtonClick selection: " + pickerRange.getSelection());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pickerRange.getSelection());
        mValue = TimeUtils.formatSQL(cal.toInstant(),"Europe/Paris");
        onDialogClosed(true);
    }
}
