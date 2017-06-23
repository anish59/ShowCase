package com.showcase.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anish on 23-06-2017.
 */

public class DateHelper {
    public static final String MMM_dd_yy = "MMM-dd-yy";

    public static String dateToString(Date date, String datePattern) {
        return new SimpleDateFormat(datePattern).format(date);
    }
}
