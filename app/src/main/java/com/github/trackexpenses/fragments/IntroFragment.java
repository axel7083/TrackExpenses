package com.github.trackexpenses.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.R;


public class IntroFragment extends Fragment implements ISlideOperator {

  private static final String ARG_POSITION = "slider-position";

  private int position;

  public IntroFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   *
   * @return A new instance of fragment SliderItemFragment.
   */

  public static IntroFragment newInstance(int position) {
    IntroFragment fragment = new IntroFragment();
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
    return inflater.inflate(R.layout.fragment_intro, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // set page background
   // view.setBackground(requireActivity().getDrawable(BG_IMAGE[position]));

    TextView title = view.findViewById(R.id.textView);
    TextView titleText = view.findViewById(R.id.textView2);
    ImageView imageView = view.findViewById(R.id.imageView);

    // set page title
   // title.setText(PAGE_TITLES[position]);
    // set page sub title text
   // titleText.setText(PAGE_TEXT[position]);
    // set page image
   // imageView.setImageResource(PAGE_IMAGE[position]);
  }

  @Override
  public boolean canNext() {
    return true;
  }
}
