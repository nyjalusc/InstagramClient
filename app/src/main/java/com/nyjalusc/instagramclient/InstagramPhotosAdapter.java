package com.nyjalusc.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    // View lookup cache
    public static class ViewHolder {
        RoundedImageView ivProfileImage;
        TextView tvUsername;
        TextView tvCaption;
        ImageView ivPhoto;
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
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.ivProfileImage = (RoundedImageView) convertView.findViewById(R.id.ivProfileImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Insert the model data into each of the view items
        viewHolder.tvUsername.setText(photo.username);
        viewHolder.tvCaption.setText(" -- " + photo.caption);
        // Clear out the imageView because listView might show the same photo again if its recycling
        viewHolder.ivPhoto.setImageResource(0);
        // Insert the image using picasso (sends out async request)
        Picasso.with(getContext()).load(photo.imageURL).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(viewHolder.ivPhoto);
        // The rounded image styling is done through the layout
        Picasso.with(getContext()).load(photo.profilePicURL).into(viewHolder.ivProfileImage);

        // Return the created item as a view
        return  convertView;
    }
}
