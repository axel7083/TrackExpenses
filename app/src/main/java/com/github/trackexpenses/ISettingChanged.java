package com.github.trackexpenses;

public interface ISettingChanged {
    void onAmountChange(String newValue);
    void onCurrencyChange(String value);
}
