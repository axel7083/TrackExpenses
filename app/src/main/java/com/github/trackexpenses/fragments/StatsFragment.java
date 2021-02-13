package com.github.trackexpenses.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.trackexpenses.R;
import com.github.trackexpenses.activities.MainActivity;
import com.github.trackexpenses.adapters.WeekAdapter;
import com.github.trackexpenses.models.Category;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Week;
import com.github.trackexpenses.utils.CategoryUtils;
import com.github.trackexpenses.utils.CustomBarChartRender;
import com.github.trackexpenses.utils.ExpenseUtils;
import com.github.trackexpenses.utils.WeekUtils;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.github.trackexpenses.utils.ExpenseUtils.computeWeekAllowance;
import static com.github.trackexpenses.utils.WeekUtils.getWeekLeft;


public class StatsFragment extends Fragment implements WeekAdapter.ItemClickListener {

    private static final String TAG = "FavoriteFragment";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private int arg;

    private ArrayList<Category> categories;
    private ArrayList<Pair<Long, Double>> cat_expense;
    private BarChart barChart;

    private RecyclerView weeks_overview;
    private WeekAdapter adapter;
    private TextView empty_graph_warning, total_remaining, next_allowance;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(int arg) {
        StatsFragment fragment = new StatsFragment();
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
        if (getActivity() == null) {
            Log.d(TAG, "Error getActivity NULL");
            return false;
        }

        cat_expense = ((MainActivity) getActivity()).db.getSpentPerCategory();
        categories =  ((MainActivity) getActivity()).db.getCategories();

        Log.d(TAG,"getWeeks COUNT: " + ((MainActivity) getActivity()).weeks);

        return true;
    }


    public void refresh() {

        if (getActivity() == null) {
            Log.d(TAG, "refresh - Error getActivity NULL");
            return;
        }

        if(fetchData()) {
            checkEmpty();
            barChart.setData(createChartData());
            barChart.setHighlightFullBarEnabled(false);
            adapter.updateData(((MainActivity) getActivity()).weeks);
            adapter.notifyDataSetChanged();
            setupOverView();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = view.findViewById(R.id.category_chart);
        weeks_overview = view.findViewById(R.id.weeks_overview);
        empty_graph_warning = view.findViewById(R.id.empty_graph_warning);
        total_remaining = view.findViewById(R.id.total_remaining);
        next_allowance = view.findViewById(R.id.next_allowance);

        setupChart();
        setupRV();
        setupOverView();
    }

    private void setupOverView() {
        if (getActivity() == null) {
            Log.d(TAG, "setupOverView - Error getActivity NULL");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        Pair<Double,Double> allowance = computeWeekAllowance(((MainActivity) getActivity()).weeks,((MainActivity) getActivity()).getSettings());

        total_remaining.setText("$" + df.format(allowance.first));
        next_allowance.setText("$" + df.format(allowance.second));

    }

    private void setupRV() {
        if (getActivity() == null) {
            Log.d(TAG, "setupRV - Error getActivity NULL");
            return;
        }

        adapter = new WeekAdapter(getContext(),((MainActivity) getActivity()).weeks);
        adapter.setClickListener(this);
        weeks_overview.setAdapter(adapter);
        weeks_overview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void checkEmpty() {
        if(cat_expense.size() == 0) {
            Log.d(TAG,"No data to display");
            empty_graph_warning.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            return;
        }
        else
        {
            empty_graph_warning.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
        }
    }

    private void setupChart() {

        checkEmpty();

        barChart.animateY(800);
        barChart.setData(createChartData());
        configureChartAppearance();
    }

    private void configureChartAppearance() {
        barChart.getDescription().setEnabled(false);

        // Make round corner
        CustomBarChartRender barChartRender = new CustomBarChartRender(barChart,barChart.getAnimator(), barChart.getViewPortHandler());
        barChartRender.setRadius(20);
        barChart.setRenderer(barChartRender);
        barChart.setClickable(false);
        barChart.setHighlightFullBarEnabled(false);
        //barChart.setTouchEnabled(false);

        barChart.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        barChart.setScrollBarSize(100);
        barChart.setHorizontalScrollBarEnabled(true);
        barChart.setScrollBarDefaultDelayBeforeFade(100000);
        barChart.setVisibleXRangeMaximum(8);

        barChart.setPinchZoom(false);
        barChart.setScaleYEnabled(false);
        barChart.setScaleXEnabled(false);

        barChart.enableScroll();

        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(20);


        barChart.getData().setHighlightEnabled(false); //hightlight on click
        barChart.getAxisLeft().setDrawGridLines(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(20);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        //xAxis.setAxisMinimum(-1);
        //xAxis.setAxisMaximum(7);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if((int) value < cat_expense.size() && (int) value >=0)
                    return CategoryUtils.getCategory(cat_expense.get((int) value).first,categories).smiley;
                else
                    return "";
            }
        });


        barChart.getAxisRight().setEnabled(false);
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < cat_expense.size(); i++) {
            values.add(new BarEntry(i, cat_expense.get(i).second.floatValue()));
        }
        BarDataSet set1 = new BarDataSet(values, "SET_LABEL");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);
        data.setDrawValues(true);

        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onItemClick(View view, int position) {
        //TODO: do something with it
    }
}