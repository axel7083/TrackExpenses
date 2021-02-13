package com.github.trackexpenses.utils;

import com.github.trackexpenses.models.Category;

import java.util.ArrayList;

import lombok.SneakyThrows;

public class CategoryUtils {

    public static Category getCategory(String ID, ArrayList<Category> categories) {
        for(Category cat : categories) {
            if(cat.ID.equals(ID))
                return cat;
        }
        return null;
    }

    public static Category getCategory(long ID, ArrayList<Category> categories) {
        return getCategory(ID + "",categories);
    }
}
