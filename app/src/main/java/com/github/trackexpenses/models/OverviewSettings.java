package com.github.trackexpenses.models;


import java.util.Objects;

import lombok.Data;

@Data
public class OverviewSettings {
    boolean displayRemaining = true;
    boolean displayNextWeekAllowance = true;
    boolean displaySpent;
    boolean displayPeriod;
    boolean displayTopCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverviewSettings that = (OverviewSettings) o;
        return displayRemaining == that.displayRemaining &&
                displayNextWeekAllowance == that.displayNextWeekAllowance &&
                displaySpent == that.displaySpent &&
                displayPeriod == that.displayPeriod &&
                displayTopCategory == that.displayTopCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayRemaining, displayNextWeekAllowance, displaySpent, displayPeriod, displayTopCategory);
    }
}
