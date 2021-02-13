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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;
import com.github.trackexpenses.adapters.CategoryAdapter;
import com.github.trackexpenses.models.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoriesDialog extends Dialog implements
        View.OnClickListener, CategoryAdapter.ItemClickListener {

    private static final String TAG = "LocationDialog";

    private Callback callback;

    private EditText dialog_text;
    private TextView btn_cancel;

    private RecyclerView RVAutoComplete;
    private Activity activity;
    private CategoryAdapter adapter;

    private List<Category> categories;

    private Category newCat;

    public CategoriesDialog(Activity activity, List<Category> categories, Callback callback) {
        super(activity);
        this.activity = activity;
        this.categories = categories;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.categories_dialog);

        setupViews();
    }

    private void setupViews()
    {
        dialog_text = findViewById(R.id.dialog_text);
        dialog_text.requestFocus();
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        dialog_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0)
                {
                    adapter.updateData(categories);
                    adapter.notifyDataSetChanged();
                    newCat = null;
                    return;
                }

                List<Category> bis = new ArrayList<>();
                for(Category cat : categories) {
                    if(cat.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                        bis.add(cat);
                }

                if(bis.size() != 0) {
                    adapter.updateData(bis);
                    adapter.notifyDataSetChanged();
                    newCat = null;
                }
                else
                {
                    newCat = new Category(null,"Add \"" + charSequence + "\" category","+");
                    bis.add(newCat);
                    adapter.updateData(bis);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        RVAutoComplete = findViewById(R.id.RVAutoComplete);


        btn_cancel = findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(this);

        // set up the RecyclerView
        RVAutoComplete.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(getContext());
        adapter.updateData(categories);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(this);
        RVAutoComplete.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_cancel:
                dismiss();
                break;
        }

    }

    @Override
    public void dismiss() {
        Log.d(TAG,"dismiss");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dialog_text.getWindowToken(), 0);

        super.dismiss();
    }

    @Override
    public void onItemClick(View view, String ID) {
        Log.d(TAG,"onItemClick ID " + ID);

        if(newCat != null) {
            Toast.makeText(getContext(),dialog_text.getText() + " added.",Toast.LENGTH_SHORT).show();
            newCat.setName(dialog_text.getText().toString());
            callback.addCategory(newCat);
        }
        else {
            callback.selectCategory(ID);
        }

        dismiss();
    }

    public interface Callback {
            void addCategory(Category category);
            void selectCategory(String ID);
    }
}