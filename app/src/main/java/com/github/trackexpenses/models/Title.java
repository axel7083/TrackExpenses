package com.github.trackexpenses.models;

import com.github.trackexpenses.IItems;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Title implements IItems {

    public String title;

    @Override
    public boolean isTitle() {
        return true;
    }
}
