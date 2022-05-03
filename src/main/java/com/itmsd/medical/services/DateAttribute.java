package com.itmsd.medical.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAttribute {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String getTime(Date currentDate) {
 
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // convert calendar to date
        Date currentDateUpdate = c.getTime();
       // System.out.println(dateFormat.format(currentDatePlusOne));
        String dateNow = dateFormat.format(currentDateUpdate).toString();
        return dateNow;
    }
    public static Date getDate(String currentDate) throws ParseException {
    	return dateFormat.parse(currentDate);
    	
    }
    
}
