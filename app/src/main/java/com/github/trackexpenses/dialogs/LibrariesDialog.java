package com.github.trackexpenses.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.BuildConfig;
import com.github.trackexpenses.R;
import com.github.trackexpenses.adapters.LibrariesAdapter;
import com.github.trackexpenses.models.Library;
import com.github.trackexpenses.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LibrariesDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "VersionDialog";

    private RecyclerView rv_libraries;

    private List<Library> libs;

    public LibrariesDialog(Activity activity) {
        super(activity);

        libs = new ArrayList<>();

        String[] lib_name = activity.getResources().getStringArray(R.array.libraries_name);
        String[] lib_license = activity.getResources().getStringArray(R.array.libraries_license);
        String[] lib_url = activity.getResources().getStringArray(R.array.libraries_url);

        for(int i = 0 ; i < lib_name.length; i++) {
            libs.add(new Library(lib_name[i],lib_license[i],lib_url[i]));
        }
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
        LibrariesAdapter adapter = new LibrariesAdapter(getContext(), libs);
        rv_libraries.setAdapter(adapter);
        rv_libraries.setLayoutManager(new LinearLayoutManager(getContext()));

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