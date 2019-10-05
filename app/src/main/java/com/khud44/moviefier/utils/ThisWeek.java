package com.khud44.moviefier.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ThisWeek {

    private static String firstDayOfWeek;
    private static String lastDayOfWeek;

    public ThisWeek(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.MONDAY);
        firstDayOfWeek = String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        lastDayOfWeek =  String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
    }

    public String getFirstDayOfWeek(){
        return firstDayOfWeek;
    }

    public String getLastDayOfWeek(){
        return lastDayOfWeek;
    }

}
