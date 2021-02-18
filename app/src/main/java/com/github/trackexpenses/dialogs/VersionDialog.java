package com.github.trackexpenses.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.trackexpenses.BuildConfig;
import com.github.trackexpenses.R;
import com.github.trackexpenses.adapters.CategoryAdapter;
import com.github.trackexpenses.models.Category;
import com.github.trackexpenses.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class VersionDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "VersionDialog";

    private TextView version, build_date, build_type;

    public VersionDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_version);

        setupViews();
    }

    private void setupViews()
    {
        version = findViewById(R.id.version);
        build_date = findViewById(R.id.build_date);
        build_type = findViewById(R.id.build_type);

        version.setText(BuildConfig.VERSION_NAME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(BuildConfig.BUILD_TIME);
        build_date.setText(TimeUtils.formatTitle(calendar.toInstant(),"Europe/Paris", true));
        build_type.setText(BuildConfig.BUILD_TYPE);


        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_close:
                dismiss();
                break;
        }

    }

    @Override
    public void dismiss() {
        Log.d(TAG,"dismiss");
        super.dismiss();
    }
}