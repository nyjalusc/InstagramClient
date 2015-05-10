package com.nyjalusc.instagramclient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class InstagramPhoto {
    public String id;
    public String username;
    public String profilePicURL;
    public String caption;
    public String imageURL;
    public int imageHeight;
    public int likesCount;
    public int commentsCount;
    public String type;
    public String createdTime;
    // <username_of_commenter, comment>
    public Map<String, String> comments;

    public static final String DARK_BLUE = "#01579b";
    public static final String BLACK = "#000000";

    // Style the integer with "," eg. 1,000
    public String getLikesCount() {
        String formattedLikesCount = NumberFormat.getNumberInstance(Locale.US).format(this.likesCount);
        return formattedLikesCount + " likes";
    }

    public ArrayList<String> getFormattedComments(int lastNComments) {
        ArrayList<String> result = new ArrayList<String>();
        int index = 0;
        if (comments.size() - lastNComments > 0) {
            index = comments.size() - lastNComments;
        } else {
            index = comments.size();
        }
        Set<String> keys = comments.keySet();
        // Convert the Set to list for ease of use
        List<String> listOfUserNames = new ArrayList<String>();
        listOfUserNames.addAll(keys);

        for (; index < comments.size(); index++) {
            String username = listOfUserNames.get(index);
            String commentText = comments.get(username);
            result.add(formatComment(username, commentText));
        }
        return result;
    }

    public String getFormattedCommentsCount() {
       return "View all "  + this.commentsCount + " comments";
    }

    public ArrayList<String> getFormattedComments() {
        return getFormattedComments(2);
    }

    public String getFormattedCaption() {
        return formatUserName() + " " + formatString(this.caption);
    }

    private String formatComment(String username, String commentText) {
        return formatUserName() + " " + formatString(commentText);
    }

    /**
     * This is to format and color the @mentions and #hashtags in comment strings
     * @param commentText
     * @return
     */
    private String formatString(String commentText) {
        StringBuilder sb = new StringBuilder();
        String[] words = commentText.split(" ");
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

    /**
     * Style the username
     * @return
     */
    private String formatUserName() {
        // getResources().getColor(R.color.dark_blue) won't work here;
        return "<b><font color=\"" + DARK_BLUE + "\">" + this.username + "</font></b>";
    }
}
