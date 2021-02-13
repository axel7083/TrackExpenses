package com.github.trackexpenses.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.R;
import com.github.trackexpenses.utils.TimeUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.sql.Time;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;

@Getter
public class DateRangeFragment extends Fragment implements ISlideOperator {

  private static final String ARG_POSITION = "slider-position";

  private int position;
  private boolean canNext;

  public String startFormatted;
  public String endFormatted;

  public DateRangeFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   *
   * @return A new instance of fragment SliderItemFragment.
   */

  public static DateRangeFragment newInstance(int position) {
    DateRangeFragment fragment = new DateRangeFragment();
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
    return inflater.inflate(R.layout.fragment_date_range, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // set page background
    //view.setBackground(requireActivity().getDrawable(BG_IMAGE[position]));

    TextView title = view.findViewById(R.id.textView);
    TextView titleText = view.findViewById(R.id.textView2);

    view.findViewById(R.id.btn_calendar).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTheme(R.style.ThemeOverlayCatalogMaterialCalendarCustom);
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(new CalendarConstraints.DateValidator() {
          @Override
          public boolean isValid(long date) {
            Calendar pick = Calendar.getInstance();
            pick.setTimeInMillis(date);
            return !pick.before(TimeUtils.getNow());
          }

          @Override
          public int describeContents() {
            return 0;
          }

          @Override
          public void writeToParcel(Parcel parcel, int i) {

          }
        });
        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();


        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {

          @Override
          public void onPositiveButtonClick(Pair<Long, Long> selection) {

            title.setText("Period selected");
            Long startDate = selection.first;
            Long endDate = selection.second;

            Calendar calStart = Calendar.getInstance();
            calStart.setTimeInMillis(startDate);
            calStart.set(Calendar.HOUR,0);
            calStart.set(Calendar.MINUTE,0);
            calStart.set(Calendar.MINUTE,0);
            calStart.set(Calendar.MILLISECOND,1);
            startFormatted = TimeUtils.formatSQL(calStart.toInstant(),"Europe/Paris");

            Calendar calEnd = Calendar.getInstance();
            calEnd.setTimeInMillis(endDate);
            calEnd.set(Calendar.HOUR,23);
            calEnd.set(Calendar.MINUTE,59);
            calEnd.set(Calendar.MINUTE,59);
            calEnd.set(Calendar.MILLISECOND,999);
            endFormatted = TimeUtils.formatSQL(calEnd.toInstant(),"Europe/Paris");

            titleText.setText(String.format("%s - %s",
                    TimeUtils.formatTitle(calStart.toInstant(), "Europe/Paris",true),
                    TimeUtils.formatTitle(calEnd.toInstant(), "Europe/Paris", true)
            ));

            canNext = true;
          }
        });
        picker.show(getActivity().getSupportFragmentManager(),picker.toString());
      }
    });

    // set page title
   // title.setText(PAGE_TITLES[position]);
    // set page sub title text
   // titleText.setText(PAGE_TEXT[position]);
    // set page image
   // imageView.setImageResource(PAGE_IMAGE[position]);
  }

  @Override
  public boolean canNext() {
    return canNext;
  }
}
