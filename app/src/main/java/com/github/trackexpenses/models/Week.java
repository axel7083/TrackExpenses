package com.github.trackexpenses.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Week {
    public double goal;
    public String date;
    public double spent;

    public Week(double goal, String date, double spent) {
        this.goal = goal;
        this.date = date;
        this.spent = spent;
    }
}
