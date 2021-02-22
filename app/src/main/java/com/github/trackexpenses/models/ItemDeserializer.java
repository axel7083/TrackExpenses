package com.github.trackexpenses.models;

import android.util.Log;

import com.github.trackexpenses.IItems;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ItemDeserializer implements JsonDeserializer<IItems> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public IItems deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG,typeOfT.getClass().getName());
        Log.d(TAG,json.toString());

        if(json.getAsJsonObject().get("ID") != null)
            return new Gson().fromJson(json,Expense.class);
        else
            return new Gson().fromJson(json,Title.class);

    }
}
