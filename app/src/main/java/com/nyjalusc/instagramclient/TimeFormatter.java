package com.nyjalusc.instagramclient;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Weeks;

public class TimeFormatter {

    public String getTimeShort(String epoch) {
        String relativeTime = getTime(epoch);
        String[] words = relativeTime.split(" ");
        return words[0] + words[1].charAt(0);
    }

    public String getTime(String epoch) {
        try {
            DateTime dt1 = new DateTime(Long.parseLong(epoch) * 1000);
            DateTime dt2 = new DateTime();
            int relativeTime = Weeks.weeksBetween(dt1, dt2).getWeeks();
            if (relativeTime > 0) {
                return prettyRelaiveTimeStamp(relativeTime, "weeks");
            }

            relativeTime = Days.daysBetween(dt1, dt2).getDays();
            if (relativeTime > 0) {
                return prettyRelaiveTimeStamp(relativeTime, "days");
            }

            relativeTime = Hours.hoursBetween(dt1, dt2).getHours();
            if (relativeTime > 0) {
                return prettyRelaiveTimeStamp(relativeTime, "hours");
            }

            relativeTime = Minutes.minutesBetween(dt1, dt2).getMinutes();
            if (relativeTime > 0) {
                return prettyRelaiveTimeStamp(relativeTime, "minutes");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String prettyRelaiveTimeStamp(int relativeTime, String duration) {
        String appendStr;
        if (relativeTime == 1) {
            appendStr = duration.substring(0, duration.length() - 1) + " ago";
        } else {
            appendStr = duration + " ago";
        }
        return relativeTime + " " + appendStr;
    }

}
