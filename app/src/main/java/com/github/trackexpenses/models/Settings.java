package com.github.trackexpenses.models;

import java.util.Objects;

import lombok.Data;

@Data
public class Settings {
    public double amount;
    public String currency;
    public String startFormatted;
    public String endFormatted;
    public String version;
    public OverviewSettings overviewSettings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Double.compare(settings.amount, amount) == 0 &&
                Objects.equals(currency, settings.currency) &&
                Objects.equals(startFormatted, settings.startFormatted) &&
                Objects.equals(endFormatted, settings.endFormatted) &&
                Objects.equals(version, settings.version) &&
                Objects.equals(overviewSettings, settings.overviewSettings);
    }

    public boolean compareAmount(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Double.compare(settings.amount, amount) == 0;
    }

    public boolean compareEndDate(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(endFormatted, settings.endFormatted);
    }

    public boolean compareCurrency(Object o)  {
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(currency, settings.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency, startFormatted, endFormatted, version);
    }
}
