package com.nyjalusc.instagramclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class PhotosActivity extends Activity {

    public static final String CLIENT_ID = "04fa4220195c4f3fa75611784ff3842a";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    private TimeFormatter timeFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        // Send out the API Request to popular photos
        photos = new ArrayList<InstagramPhoto>();
        // Create the Adapter
        aPhotos = new InstagramPhotosAdapter(this, photos);
       // Find the listView from the adapter
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        // Connect the adapter to the listView
        lvPhotos.setAdapter(aPhotos);
        // Fetch the photos
        fetchPopularPhotos();
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos();

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        timeFormatter = new TimeFormatter();
    }

    // Trgger API request
    public void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        // Create the client
        AsyncHttpClient client = new AsyncHttpClient();
        // Trigger the GET Request
        client.get(url, null, new JsonHttpResponseHandler() {
            // onSuccess (Worked, 200)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Expecting a JSONobject
                // Type:  { “data” => [x] => “type”} (“image” or “video”)
                // URL:  {“data” => [x] => “images” => “standard_resolution” => “url"}
                // Caption: {“data” => [x] => “caption” => “text”}
                // Author:  {“data” => [x] => “user” => “username”}

                // This is done to refresh the contents of the adapter
                aPhotos.clear();
                JSONArray photosJSON = null;
                try {
                   photosJSON = response.getJSONArray("data"); // Get the data
                   // iterate
                   for (int i=0; i < photosJSON.length(); i++) {
                       // Get the json object
                       JSONObject photoJSON = photosJSON.getJSONObject(i);
                       // Decode the attribute of the JSON in data model
                       InstagramPhoto photo = new InstagramPhoto();
                       // Author:  {“data” => [x] => “user” => “username”}
                       photo.username = photoJSON.getJSONObject("user").getString("username");
                       photo.profilePicURL = photoJSON.getJSONObject("user").getString("profile_picture");
                       // Caption: {“data” => [x] => “caption” => “text”}
                       // Caption: {“data” => [x] => “caption” => “text”}
                       if (photoJSON.optJSONObject("caption") != null) {
                           photo.caption = photoJSON.getJSONObject("caption").getString("text");
                       } else {
                           photo.caption = "";
                       }
                       // URL:  {“data” => [x] => “images” => “standard_resolution” => “url"}
                       photo.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                       // Type:  { “data” => [x] => “type”} (“image” or “video”)
                       photo.type = photoJSON.getString("type");
                       photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                       photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                       photo.createdTime = photoJSON.getString("created_time");

                       // COMMENTS
                       JSONArray comments = photoJSON.getJSONObject("comments").getJSONArray("data");
                       // Initialize the linkedHashMap..This is to presever the ordering
                       // The ordering will be used later to fetch the latest N comments
                       photo.comments = new LinkedHashMap<String, String>();
                       // Add all comments to the arraylist
                       for(int j=0; j < comments.length(); j++) {
                           JSONObject comment = comments.getJSONObject(j);
                           String commentText = comment.getString("text");
                           JSONObject commenterInfo = comment.getJSONObject("from");
                           String commenterName = commenterInfo.getString("username");
                           photo.comments.put(commenterName, commentText);
                       }

                       timeFormatter.getTime(photo.createdTime);

                       // Add to the arraylist
                       photos.add(photo);
                   }
                } catch (JSONException e) {
                   e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                // Callback refreshes the listView
                aPhotos.notifyDataSetChanged();
            }

            // onFailure (fail)
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
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
