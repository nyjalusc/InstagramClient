package com.nyjalusc.instagramclient;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstagramCommentsAdapter extends ArrayAdapter<InstagramComment> {
    TimeFormatter timeFormatter;
    // View lookup cache
    public static class ViewHolder {
        RoundedImageView ivProfileImage;
        TextView tvCommentText;
        TextView tvTimeElapsed;
    }

    public InstagramCommentsAdapter(Context context, List<InstagramComment> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        timeFormatter = new TimeFormatter();
    }

    // Use the template to display each comment
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // Get the data item for this position
        InstagramComment comment = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.polished_comment_row, parent, false);
            viewHolder.ivProfileImage = (RoundedImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvCommentText = (TextView) convertView.findViewById(R.id.tvCommentText);
            viewHolder.tvTimeElapsed = (TextView) convertView.findViewById(R.id.tvTimeElapsed);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvCommentText.setText(Html.fromHtml(comment.getComment()));
        viewHolder.tvTimeElapsed.setText(timeFormatter.getTime(comment.createdTime));
        Picasso.with(getContext()).load(comment.profilePicURL).into(viewHolder.ivProfileImage);

        return convertView;
    }
}
