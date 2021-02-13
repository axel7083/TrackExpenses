package com.github.trackexpenses.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.R;

import info.hoang8f.android.segmented.SegmentedGroup;
import lombok.Getter;


@Getter
public class CurrencyPickerFragment extends Fragment implements ISlideOperator, RadioGroup.OnCheckedChangeListener {

    private static final String ARG_POSITION = "slider-position";
    private static final String TAG = "CurrencyPickerFragment";

    private int position;
    private boolean canNext;
    public String currency;
    private SegmentedGroup segmentedGroup;

    public CurrencyPickerFragment() {
      // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment SliderItemFragment.
     */

    public static CurrencyPickerFragment newInstance(int position) {
      CurrencyPickerFragment fragment = new CurrencyPickerFragment();
      Bundle args = new Bundle();
      args.putInt(ARG_POSITION, position);
      fragment.setArguments(args);
      return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
        position = getArguments().getInt(ARG_POSITION);
      }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_currency_picker, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

        segmentedGroup = (SegmentedGroup) view.findViewById(R.id.segmented);
        segmentedGroup.setOnCheckedChangeListener(this);

      /*view.findViewById(R.id.btn_currency).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          showCurrencyDialog();
        }
      });*/
    }


    @Override
    public boolean canNext() {
      return canNext;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radio_euro:
                currency = getString(R.string.euro_sign);
                break;
            case R.id.radio_dollar:
                currency = getString(R.string.dollar_sign);
                break;
            case R.id.radio_mark:
                currency = getString(R.string.mark_sign);
                break;
        }
        canNext = true;
    }
}
