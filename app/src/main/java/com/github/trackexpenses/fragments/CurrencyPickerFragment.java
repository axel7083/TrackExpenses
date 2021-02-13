package com.github.trackexpenses.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.R;

import lombok.Getter;


@Getter
public class CurrencyPickerFragment extends Fragment implements ISlideOperator {

  private static final String ARG_POSITION = "slider-position";

  private int position;
  private boolean canNext;
  public String currency;

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


    view.findViewById(R.id.btn_currency).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //TODO: Open currency dialog
      }
    });
  }

  @Override
  public boolean canNext() {
    return false;
  }
}
