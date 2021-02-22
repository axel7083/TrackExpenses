package com.github.trackexpenses.utils;

import com.github.trackexpenses.IItems;
import com.github.trackexpenses.models.Expense;
import com.github.trackexpenses.models.Settings;
import com.github.trackexpenses.models.Title;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class TimeUtils {

    public static final String SQL_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SIMPLE_PATTERN = "yyyy-MM-dd";
    public static final String TITLE_PATTERN = "EEE dd";
    public static final String SHORT_PATTERN = "dd MMM yyyy";

    public static Calendar getFirstDayOfWeek(String timeZone) {
        int dayOfWeek = Instant.now().atZone(ZoneId.of(timeZone)).getDayOfWeek().getValue(); // FROM 1 to 7

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR,-1*(dayOfWeek-1)); //Reset at monday
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getLastDayOfWeek(String timeZone) {
        Calendar calendar = getFirstDayOfWeek(timeZone);
        calendar.add(Calendar.DAY_OF_YEAR,6); //set at sunday
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static Calendar getMonday(String date, String timeZone) {
        return getMonday(date,timeZone,SQL_PATTERN);
    }

    public static Calendar getMonday(String date, String timeZone, String pattern) {
        Calendar cal = toCalendar(date, pattern);
        int dayOfWeek = cal.toInstant().atZone(ZoneId.of(timeZone)).getDayOfWeek().getValue();
        cal.add(Calendar.DAY_OF_YEAR,-1*(dayOfWeek-1)); //Reset at monday
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getSunday(String date, String timeZone) {
        return getSunday(date,timeZone,SQL_PATTERN);
    }

    public static Calendar getSunday(String date, String timeZone, String pattern) {
        Calendar cal = toCalendar(date, pattern);
        int dayOfWeek = cal.toInstant().atZone(ZoneId.of(timeZone)).getDayOfWeek().getValue();
        cal.add(Calendar.DAY_OF_YEAR,-1*(dayOfWeek-1) + 6); //Reset at sunday
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal;
    }

    public static Calendar getNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static boolean isEnded(Settings settings) {
        Calendar now = getNow();
        Calendar end = toCalendar(settings.endFormatted);
        return end.before(now);
    }

    public static boolean isStarted(Settings settings) {
        Calendar now = getNow();
        Calendar start = toCalendar(settings.startFormatted);

        //If is same day.
        if(formatSimple(now.toInstant(),"Europe/Paris").equals(formatSimple(start.toInstant(),"Europe/Paris")))
            return true;

        return start.before(now);
    }

    public static String formatSQL(Instant instant_start, String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime date_start = instant_start.atZone(zone);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(SQL_PATTERN);
        return date_start.format(formatter2);
    }

    public static String formatTitle(Instant instant_start, String timezone, Boolean includedDetails) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime date_start = instant_start.atZone(zone);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(TITLE_PATTERN + (includedDetails?" MMM yyyy":""));
        return date_start.format(formatter2);
    }

    public static String formatShort(Instant instant_start, String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime date_start = instant_start.atZone(zone);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(SHORT_PATTERN);
        return date_start.format(formatter2);
    }

    public static String formatSimple(Instant instant_start, String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime date_start = instant_start.atZone(zone);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(SIMPLE_PATTERN);
        return date_start.format(formatter2);
    }

    @SneakyThrows
    public static Calendar toCalendar(String str) {
        return toCalendar(str, SQL_PATTERN);
    }

    @SneakyThrows
    public static Calendar toCalendar(String str, String pattern) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        cal.setTime(sdf.parse(str));// all done
        return cal;
    }

    public static long getCountWeekLeft(String end) {
        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(System.currentTimeMillis());
        Calendar calEnd = toCalendar(end);
        return getFullWeeks(calNow, calEnd);
    }

    public static long getFullWeeks(Calendar d1, Calendar d2){

        Instant d1i = Instant.ofEpochMilli(d1.getTimeInMillis());
        Instant d2i = Instant.ofEpochMilli(d2.getTimeInMillis());

        LocalDateTime startDate = LocalDateTime.ofInstant(d1i, ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(d2i, ZoneId.systemDefault());

        return ChronoUnit.WEEKS.between(startDate, endDate);
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static ArrayList<IItems> separateWithTitle(ArrayList<Expense> expenses, boolean includeDetails) {
        ArrayList<IItems> items = new ArrayList<>();

        if(expenses.isEmpty())
            return null;

        Calendar date = toCalendar(expenses.get(0).getDate());
        items.add(new Title(formatTitle(date.toInstant(),"Europe/Paris",includeDetails)));
        items.add(expenses.get(0));

        for(int i = 1 ; i  < expenses.size(); i++) {
            Calendar newCalendar = toCalendar(expenses.get(i).getDate());

            if(newCalendar.get(Calendar.DAY_OF_WEEK) != date.get(Calendar.DAY_OF_WEEK))
            {
                date = newCalendar;
                items.add(new Title(formatTitle(date.toInstant(),"Europe/Paris",includeDetails)));
            }
            items.add(expenses.get(i));
        }

        return items;
    }
}
