package com.khud44.moviefier.data;

import android.arch.persistence.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    // convert json string from db to arraylist of ints
    @TypeConverter
    public static List<Integer> fromString(String value) {
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    // convert arraylist of ints to json string
    @TypeConverter
    public static String fromArrayList(List<Integer> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
