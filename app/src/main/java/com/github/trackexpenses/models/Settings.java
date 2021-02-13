package com.github.trackexpenses.models;

import lombok.Data;

@Data
public class Settings {
    public double amount;
    public String currency;
    public String startFormatted;
    public String endFormatted;
}
