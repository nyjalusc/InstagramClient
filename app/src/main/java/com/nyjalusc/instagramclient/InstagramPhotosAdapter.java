package com.nyjalusc.instagramclient;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private static final TimeFormatter timeFormatter = new TimeFormatter();

    // View lookup cache
    public static class ViewHolder {
        RoundedImageView ivProfileImage;
        TextView tvUsername;
        TextView tvCaption;
        ImageView ivPhoto;
        TextView tvTimeElapsed;
        TextView tvLikesCount;
        TextView tvCommentsCount;
        ImageView ivPlayLogo;
        ImageView ivLocationPicker;
        TextView tvLocation;
        LinearLayout commentsHolder;
    }

    // What data we need from the activity
    // Context, Data source
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    // Use the template to display each photo
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // Get the data item for this position
        InstagramPhoto photo = getItem(position);
        // Check if we are using recycled view, if not we need to inflate
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // Create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            // Lookup the views for populating the data
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.ivLocationPicker = (ImageView) convertView.findViewById(R.id.ivLocationPicker);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.ivPlayLogo = (ImageView) convertView.findViewById(R.id.ivPlayLogo);
            viewHolder.ivProfileImage = (RoundedImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvTimeElapsed = (TextView) convertView.findViewById(R.id.tvTimeElapsed);
            viewHolder.tvLikesCount = (TextView) convertView.findViewById(R.id.tvLikesCount);
            viewHolder.tvCommentsCount = (TextView) convertView.findViewById(R.id.tvCommentsCount);
            viewHolder.commentsHolder = (LinearLayout) convertView.findViewById(R.id.commentsHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Insert the model data into each of the view items
        viewHolder.tvUsername.setText(photo.username);
        viewHolder.tvCaption.setText(Html.fromHtml(photo.getFormattedCaption()));

        // By default it should be invsible; Explicitly do this to reset the visibility
        viewHolder.ivLocationPicker.setVisibility(View.GONE);
        viewHolder.tvLocation.setVisibility(View.GONE);
        // If there is a geo-tagged location, enable the view and populate data
        if (photo.location != null) {
            viewHolder.tvLocation.setText(photo.location);
            viewHolder.ivLocationPicker.setVisibility(View.VISIBLE);
            viewHolder.tvLocation.setVisibility(View.VISIBLE);
        }

        // Clear out the imageView because listView might show the same photo again if its recycling
        viewHolder.ivPhoto.setImageResource(0);
        // Insert the image using picasso (sends out async request)
//        Picasso.with(getContext()).load(photo.imageURL).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(viewHolder.ivPhoto);
        Picasso.with(getContext()).load(photo.imageURL).fit().centerCrop().placeholder(R.drawable.loading_larger).into(viewHolder.ivPhoto);
        // The rounded image styling is done through the layout
        Picasso.with(getContext()).load(photo.profilePicURL).into(viewHolder.ivProfileImage);

        // Make the video play logo disapper if type = image otherwise embed the videoURL as a tag
        // the url will be used later to play the video if the user clicks on the image
        // Explicitly set the visibility of play logo for every item
        if (!photo.isVideo()) {
            viewHolder.ivPlayLogo.setVisibility(View.GONE);
            viewHolder.ivPhoto.setTag(null);
        } else {
            viewHolder.ivPlayLogo.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setTag(photo.videoURL);
        }

        // Time is formatted in terms of weeks, days, hours and mintues (Like instagram does)
        String formattedTime = timeFormatter.getTimeShort(photo.createdTime);
        viewHolder.tvTimeElapsed.setText(formattedTime);

        // Set the text for displaying likes count
        viewHolder.tvLikesCount.setText(photo.getLikesCount());

        // Set the text for displaying comments count
        viewHolder.tvCommentsCount.setText(photo.getFormattedCommentsCount());
        viewHolder.tvCommentsCount.setTag(photo.id);

        // Remove all the old views
        viewHolder.commentsHolder.removeAllViews();
        // Show the last 2 comments
        for(String commentText : photo.getFormattedComments()) {
            View commentRow = LayoutInflater.from(getContext()).inflate(R.layout.comment_row, parent, false);
            LinearLayout llCommentRow = (LinearLayout) commentRow.findViewById(R.id.llCommentsHolder);
            TextView tvComment = (TextView) commentRow.findViewById(R.id.tvComment);
            tvComment.setText(Html.fromHtml(commentText));
            // IMPORTANT: Add the the whole container (along with LinearLayout) and not just the textView (child)
            viewHolder.commentsHolder.addView(llCommentRow);
        }
        // Return the created item as a view
        return  convertView;
    }
}
