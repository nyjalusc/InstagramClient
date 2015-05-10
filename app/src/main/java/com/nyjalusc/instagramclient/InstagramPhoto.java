package com.nyjalusc.instagramclient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// TODO: This class should be renamed to InstagramMedia
public class InstagramPhoto {
    public String id;
    public String username;
    public String profilePicURL;
    public String caption;
    public String imageURL;
    public String videoURL;
    public String mediaURL;
    public int imageHeight;
    public int likesCount;
    public int commentsCount;
    public String type;
    public String createdTime;
    // TODO: Comments should be represented as ArrayList<InstagramComment>
    // <username_of_commenter, comment>
    public Map<String, String> comments;
    public String location;

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

    public boolean isVideo() {
        return this.type.equals("video");
    }

    private String formatComment(String username, String commentText) {
        return formatUserName() + " " + formatString(commentText);
    }

    // This is to format and color the @mentions and #hashtags in comment strings
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

    // Style the username
    private String formatUserName() {
        // getResources().getColor(R.color.dark_blue) won't work here;
        return "<b><font color=\"" + DARK_BLUE + "\">" + this.username + "</font></b>";
    }
}
