package com.example.android.popularmoviesst2.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getLocalizedDate(Context context, String date, String format)
            throws ParseException {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        return dateFormat.format(getFormattedDate(date, format));
    }

    private static Date getFormattedDate(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.parse(date);
    }
}