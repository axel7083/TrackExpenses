package com.github.trackexpenses.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.BuildConfig;
import com.github.trackexpenses.R;
import com.github.trackexpenses.utils.TimeUtils;

import java.util.Calendar;


public class LibrariesDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "VersionDialog";

    private RecyclerView rv_libraries;

    public LibrariesDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_librairies);

        setupViews();
    }

    private void setupViews()
    {
        rv_libraries = findViewById(R.id.rv_libraries);


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