package com.nyjalusc.instagramclient;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.Period;

/**
 * Created by naugustine on 5/7/15.
 */
public class TimeFormatter {

    public String getTime(String epoch) {
        try {
            DateTime dt1 = new DateTime(Long.parseLong(epoch) * 1000);
            DateTime dt2 = new DateTime();
            Interval interval = new Interval(dt1, dt2);
            Period period = interval.toPeriod();
            int days = Days.daysBetween(dt1, dt2).getDays();
            if (days < 7) {
                if (days < 1) {
                    int hours = Hours.hoursBetween(dt1, dt2).getHours();
                    if (hours < 1) {
                        int minutes = Minutes.minutesBetween(dt1, dt2).getMinutes();
                        return (new Integer(minutes).toString() + "m");
                    } else {
                        return (new Integer(hours).toString() + "h");
                    }
                } else {
                    return (new Integer(days).toString() + "d");
                }
            } else {
                int weeks = days/7;
                return (new Integer(weeks).toString() + "w");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
