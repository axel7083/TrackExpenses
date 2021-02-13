package com.github.trackexpenses.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Week {
    public String ID;
    public double goal;
    public String date;
    public double spent;

    public Week(String ID, double goal, String date, double spent) {
        this.ID = ID;
        this.goal = goal;
        this.date = date;
        this.spent = spent;
    }
}
