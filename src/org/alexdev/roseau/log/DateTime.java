package org.alexdev.roseau.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTime {
   
	/*
	 * Jordan - Crowley's DateTime class.
	 */
	
	public static Date date() {
        return GregorianCalendar.getInstance().getTime();
    }
	
	public static Calendar calendar() {
        return GregorianCalendar.getInstance();
    }

	public static Calendar calendar(Date time) {
		Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(time); 
        return cal;
    }
	
	public static Calendar calendar(long time) {
		Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(time); 
        return cal;
    }
	
	
	public static Calendar calendar(int time) {
		Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }
	
    public static Integer seconds(int time) {
    	return calendar(time).get(Calendar.SECOND);
    }
    
    public static Integer seconds(Date time) {
    	return calendar(time).get(Calendar.SECOND);
    }
    
    public static Integer minutes(int time) {
    	return calendar(time).get(Calendar.MINUTE);
    }
    
    public static Integer minutes(Date time) {
    	return calendar(time).get(Calendar.MINUTE);
    }
    
    public static Integer hours(int time) {
    	return calendar(time).get(Calendar.HOUR);
    }
    
    public static Integer hours(Date time) {
    	return calendar(time).get(Calendar.HOUR);
    }

    public static String now() {
        return toCleanString(date());
    }

    public static Date addHours(int hours) {
        return addHours(date(), hours);
    }

    public static Date addHours(Date time, int hours) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    public static String toString(Date d) {
        return (new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")).format(d);
    }
    
    public static String toCleanString(Date d) {
        return (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(d);
    }

    public static Date fromString(String d) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date();
    }
}
