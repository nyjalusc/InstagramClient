package com.nyjalusc.instagramclient;

import android.text.format.DateUtils;

public class InstagramComment {
    public String createdTime;
    public String username;
    public String profilePicURL;
    public String text;

    public static final String DARK_BLUE = "#01579b";
    public static final String BLACK = "#000000";

    public String getComment() {
        return formatUserName() + " " + formatComment();
    }

    public String getRelativeTimeStamp() {
        Long createdTimeInMillis = Long.parseLong(createdTime) * 1000;
        Long currentTime = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(createdTimeInMillis, currentTime, DateUtils.HOUR_IN_MILLIS).toString();
    }
    // This is to format and color the @mentions and #hashtags in comment strings
    private String formatComment() {
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");
        for (String s : words) {
            if (s.contains("@") || s.contains("#")) {
                s = "<font color=\"" + DARK_BLUE + "\">" + s + "</font> ";
            } else {
                s = "<font color=\"" + BLACK + "\">" + s + "</font> ";
            }
            sb.append(s);
        }
        return sb.toString();
    }

    // Style the username
    private String formatUserName() {
        // getResources().getColor(R.color.dark_blue) won't work here;
        return "<b><font color=\"" + DARK_BLUE + "\">" + this.username + "</font></b>";
    }
}
