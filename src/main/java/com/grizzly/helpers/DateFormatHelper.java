package com.grizzly.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Samarth 9/25/16
 */

/**This class will create date strings in the required format : (yyyy/mm/dd) */
public class DateFormatHelper {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY/MM/dd");

    /** This method returns todays date in the required format*/
    public static String getToday(){
        return DATE_FORMAT.format(new Date()).toString();
    }

    /** This method returns today-2months date in required format */
    public static String getDateBeforeTwoMonths(){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH,-2);
        return DATE_FORMAT.format(now.getTime());
    }

}
