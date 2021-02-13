package com.github.trackexpenses.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.trackexpenses.ISlideOperator;
import com.github.trackexpenses.fragments.AmountFragment;
import com.github.trackexpenses.fragments.CurrencyPickerFragment;
import com.github.trackexpenses.fragments.DateRangeFragment;
import com.github.trackexpenses.fragments.IntroFragment;


public class SliderPagerAdapter extends FragmentPagerAdapter implements ISlideOperator {


  public SliderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
    super(fm, behavior);
  }

  private IntroFragment sliderItemFragment;
  private DateRangeFragment dateRangeFragment;
  private CurrencyPickerFragment currencyPickerFragment;
  private AmountFragment amountFragment;


  @NonNull @Override public Fragment getItem(int position) {
    switch (position) {
      case 0:
        if(sliderItemFragment == null)
          sliderItemFragment = IntroFragment.newInstance(position);
        return sliderItemFragment;
      case 1:
        if(dateRangeFragment == null)
          dateRangeFragment = DateRangeFragment.newInstance(position);
        return dateRangeFragment;
      case 2:
        if(currencyPickerFragment == null)
        currencyPickerFragment = CurrencyPickerFragment.newInstance(position);
      return currencyPickerFragment;
      case 3:
        if(amountFragment == null)
          amountFragment = AmountFragment.newInstance(position);
        return amountFragment;
      default:
        return IntroFragment.newInstance(position);
    }
  }

  @Override public int getCount() {
    return 4;
  }

  @Override
  public boolean canNext() {
    return false;
  }
}