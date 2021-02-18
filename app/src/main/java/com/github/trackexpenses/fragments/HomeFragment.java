package com.github.trackexpenses.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;
import com.github.trackexpenses.activities.ExpenseActivity;
import com.github.trackexpenses.activities.MainActivity;
import com.github.trackexpenses.adapters.ExpenseViewHolder;
import com.github.trackexpenses.adapters.MultipleViewAdapter;
import com.github.trackexpenses.models.Category;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Week;
import com.github.trackexpenses.utils.TimeUtils;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.trackexpenses.utils.WeekUtils.getNow;


public class HomeFragment extends Fragment implements ExpenseViewHolder.ExpenseClickListener {

    private static final String TAG = "FavoriteFragment";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private int arg;


    private RecyclerView week_overview;
    private ProgressBar now_progress;
    private TextView expense_home, remaining, empty_warning;
    private CardView card_info;
    private MultipleViewAdapter adapter;

    private ArrayList<Object> items;
    private ArrayList<Category> categories;

    private Week now;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(int arg) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, new Gson().toJson(arg));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        if (getArguments() != null) {
            arg = new Gson().fromJson(getArguments().getString(ARG_PARAM1), Integer.class);
        }

        fetchData();
    }

    public boolean fetchData() {

        if(getActivity() == null) {
            Log.d(TAG,"Error getActivity NULL");
            return false;
        }

        now = getNow(((MainActivity) getActivity()).weeks);
        //fetch data
        ArrayList<Expense> weekly_expenses =  ((MainActivity) getActivity()).db.getCurrentWeekExpenses();
        categories =  ((MainActivity) getActivity()).db.getCategories();
        items = TimeUtils.separateWithTitle(weekly_expenses);
        return true;
    }

    public void refresh() {
        Log.d(TAG,"refresh");
        boolean wasNul = false;
        if(now == null) {
            setupView();
            wasNul = true;
        }

        if(fetchData()) {
            if(wasNul) {
                setupView();
            }

            if(now == null) {
                if (getView() == null)
                    return;
                setupView();
            }
            else
                updateCardView();

            Log.d(TAG,"NOW: " + now);

            if(adapter != null) {
                adapter.setCurrency(((MainActivity)getActivity()).currency);
                adapter.setItems(items);
                adapter.setCategories(categories);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        week_overview = view.findViewById(R.id.current_week_expenses);
        now_progress = view.findViewById(R.id.now_progress);
        expense_home = view.findViewById(R.id.expense_home);
        remaining = view.findViewById(R.id.remaining);
        card_info = view.findViewById(R.id.card_info);
        empty_warning = view.findViewById(R.id.empty_warning);

        setupView();
    }

    private void setupView() {

        //This case happen ONLY if no expenses has been made this week
        if(now == null) {
            empty_warning.setVisibility(View.VISIBLE);
            card_info.setVisibility(View.GONE);
            return;
        }
        else
        {
            empty_warning.setVisibility(View.GONE);
            card_info.setVisibility(View.VISIBLE);
        }

        if(getActivity() == null) {
            Log.d(TAG,"Error getActivity NULL");
            return;
        }

        updateCardView();

        if(adapter == null)
            adapter = new MultipleViewAdapter(items, categories, ((MainActivity)getActivity()).currency, this);

        week_overview.setAdapter(adapter);
        week_overview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateCardView() {

        MainActivity main = (MainActivity) getActivity();

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        expense_home.setText(main.currency + " " + df.format(now.getSpent()));
        remaining.setText(main.currency + " " + df.format(now.getGoal()-now.getSpent()));

        if(now.getSpent() < now.getGoal())
        {
            now_progress.setMax((int) now.getGoal());
            now_progress.setProgress((int) now.getSpent());
            now_progress.setProgressTintList(ColorStateList.valueOf(getContext().getColor(R.color.purple)));
        }
        else
        {
            now_progress.setMax(100);
            now_progress.setProgress(100);
            now_progress.setProgressTintList(ColorStateList.valueOf(getContext().getColor(R.color.red)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onItemClick(View view, int position) {

        if(getActivity() == null) {
            Log.d(TAG,"onItemClick getActivity null returning.");
            return;
        }

        Log.d(TAG,"Clicked " + ((Expense) items.get(position)).getTitle() );
        Intent intent = new Intent(getActivity(), ExpenseActivity.class);
        intent.putExtra("categories", new Gson().toJson(categories));
        intent.putExtra("expense", new Gson().toJson(items.get(position)));
        intent.putExtra("settings", new Gson().toJson(((MainActivity) getActivity()).getSettings()));
        getActivity().startActivityForResult(intent, MainActivity.EXPENSE_ACTIVITY);
    }
}