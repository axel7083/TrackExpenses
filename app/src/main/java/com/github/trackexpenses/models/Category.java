package com.github.trackexpenses.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    public String ID;
    public String name;
    public String smiley;
}
