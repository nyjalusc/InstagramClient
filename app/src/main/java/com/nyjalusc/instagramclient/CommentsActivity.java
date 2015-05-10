package com.nyjalusc.instagramclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class CommentsActivity extends ActionBarActivity {

    private static final String CLIENT_ID = "04fa4220195c4f3fa75611784ff3842a";
    private ListView lvComments;
    private ArrayList<InstagramComment> comments;
    private InstagramCommentsAdapter aComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        comments = new ArrayList<InstagramComment>();
        aComments = new InstagramCommentsAdapter(this, comments);
        lvComments = (ListView) findViewById(R.id.lvComments);
        lvComments.setAdapter(aComments);
        fetchComments();
    }

    private void fetchComments() {
        String mediaId = getIntent().getStringExtra("mediaId");
        String url = "https://api.instagram.com/v1/media/" + mediaId + "/comments/?client_id=" + CLIENT_ID;
        // Create the client
        AsyncHttpClient client = new AsyncHttpClient();
        // Trigger the GET Request
        client.get(url, null, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                aComments.clear();
                JSONArray commentsJSON = null;
                try {
                    commentsJSON = response.getJSONArray("data"); // Get the data
                    for (int i = 0; i < commentsJSON.length(); i++) {
                        JSONObject commentJSON = commentsJSON.getJSONObject(i);
                        InstagramComment comment = new InstagramComment();
                        comment.createdTime = commentJSON.getString("created_time");
                        comment.text = commentJSON.getString("text");
                        comment.profilePicURL = commentJSON.getJSONObject("from").getString("profile_picture");
                        comment.username = commentJSON.getJSONObject("from").getString("username");
                        Log.d("COMMENTS", comment.username);
                        comments.add(comment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aComments.notifyDataSetChanged();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
