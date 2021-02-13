package com.github.trackexpenses.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.github.trackexpenses.ISettingChanged;
import com.github.trackexpenses.R;

/*
created this based on the implementation in the library here:
https://github.com/h6ah4i/android-numberpickerprefcompat
added default value to set in the numberPickerWheel, canceled the unitTextView
 */

public class NumberPickerPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
    private static final String SAVE_STATE_VALUE = "NumberPickerPreferenceDialogFragment.value";
    private static final String SHARED_PREFERENCES = "testandroidxpreferences"; // name for sharedPreferences location
    private EditText mNumberPicker;
    private int mValue;

    private ISettingChanged callback;

    @NonNull
    public static NumberPickerPreferenceDialogFragment newInstance(@NonNull String key, ISettingChanged callback) {
        final NumberPickerPreferenceDialogFragment fragment = new NumberPickerPreferenceDialogFragment();
        final Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // if it is first run after installation - get the default value
            mValue = getNumberPickerPreference().getDefaultValue();
        } else {
            // if not - there is a saved value
            mValue = savedInstanceState.getInt(SAVE_STATE_VALUE);
        }
    }

    // get the NumberPickerPreference instance
    private NumberPickerPreference getNumberPickerPreference() {
        return (NumberPickerPreference) getPreference();
    }

    // save the value
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_VALUE, mValue);
    }

    public NumberPickerPreferenceDialogFragment() {
        super();
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        // finding the numberPicker view
        mNumberPicker = view.findViewById(R.id.number_picker);

        // throw an IllegalStateException if there is no NumberPicker view
        if (mNumberPicker == null) {
            throw new IllegalStateException("Dialog view must contain an NumberPicker with id");
        }

        // set the values for the NumberPicker view - min, max, default/value to show and if scrollable
        mValue = Integer.parseInt(restorePreferences("number_picker_preference"));
        mNumberPicker.setText(mValue + "");

        mNumberPicker.requestFocus();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        showKeyboard();
        return super.onCreateDialog(savedInstanceState);
    }

    // This method to store the custom preferences changes
    private void savePreferences(String value) {
        callback.onAmountChange(value);
    }

    // This method to restore the custom preferences data
    private String restorePreferences(String key) {
        Activity activity = getActivity();
        SharedPreferences myPreferences;
        if (activity != null) {
            myPreferences = activity.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            if (myPreferences.contains(key))
                return myPreferences.getString(key, "");
            else return "";
        } else return "";
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    // what to do when the dialog is closed
    @Override
    public void onDialogClosed(boolean positiveResult) {

        closeKeyboard();

        if (positiveResult) {
            //mNumberPicker.clearFocus();
            final String value = String.valueOf(mNumberPicker.getText());
            savePreferences(value);

            if (getNumberPickerPreference().callChangeListener(value)) {
                getNumberPickerPreference().setValue(Integer.parseInt(value));
            }
        }
    }


    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
