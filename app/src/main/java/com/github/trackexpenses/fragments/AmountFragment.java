package com.github.trackexpenses.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.R;

import lombok.Getter;

@Getter
public class AmountFragment extends Fragment implements ISlideOperator {

  private static final String ARG_POSITION = "slider-position";

  private int position;
  private EditText amount_input;
  public double amount;
  public String currency;
  private TextView currency_amount;

  public AmountFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   *
   * @return A new instance of fragment SliderItemFragment.
   */

  public static AmountFragment newInstance(int position) {
    AmountFragment fragment = new AmountFragment();
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
    return inflater.inflate(R.layout.fragment_amount, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    amount_input = view.findViewById(R.id.amount_input);
    currency_amount = view.findViewById(R.id.currency_amount);
    amount_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
          //TODO: do something ?
        }
        return false;
      }
    });
  }

  public void refresh() {
    currency_amount.setText(currency);
  }

  @Override
  public boolean canNext() {

    amount = 0;
    try {
      amount = Double.parseDouble(amount_input.getText().toString());
    }
    catch (Exception e) {
      Toast.makeText(getContext(),"Invalid amount.", Toast.LENGTH_SHORT).show();
      return false;
    }

    if(amount == 0.0) {
      Toast.makeText(getContext(),"The amount cannot be 0.", Toast.LENGTH_SHORT).show();
      return false;
    }

    return true;
  }
}
