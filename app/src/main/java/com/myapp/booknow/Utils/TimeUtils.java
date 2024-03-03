package com.myapp.booknow.Utils;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        Date date = timestamp.toDate(); // Convert to Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static Timestamp convertToTimestamp(LocalDate date, String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(timeStr, formatter);

        // Assuming system default zone, adjust as necessary
        ZoneId zoneId = ZoneId.systemDefault();
        long epochMilli = date.atTime(time).atZone(zoneId).toInstant().toEpochMilli();

        return new Timestamp(epochMilli / 1000, (int) (epochMilli % 1000) * 1000000);
    }


    public static LocalDate timestampToLocalDate(Timestamp timestamp) {
        return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static LocalTime timestampToLocalTime(Timestamp timestamp) {
        return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static Timestamp localDateToTimestamp(LocalDate localDate) {
        // At the start of the day for the given LocalDate
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Timestamp(date);
    }


    public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        // First, convert LocalDateTime to java.util.Date
        java.util.Date date = java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        // Then, create a Firestore Timestamp from the java.util.Date
        return new Timestamp(date);
    }




}
