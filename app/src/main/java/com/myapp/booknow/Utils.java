package com.myapp.booknow;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Utils {

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
}
