package com.github.trackexpenses.models;

import com.github.trackexpenses.IItems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense implements IItems {
    public String ID;
    public String Title;
    public long Category = -1;
    public double Value;
    public String Date;

    @Override
    public boolean isTitle() {
        return false;
    }
}
