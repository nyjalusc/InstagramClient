package com.nyjalusc.instagramclient;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


public class PhotosActivity extends Activity {

    public static final String CLIENT_ID = "04fa4220195c4f3fa75611784ff3842a";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    private TimeFormatter timeFormatter;
    private ListView lvPhotos;
    private static final int REQUEST_CODE = 20;


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
    // Documentation of the endpoint can be found here: https://instagram.com/developer/endpoints/media/
    // Checkout /media/popular endpoint
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
                // This is done to refresh the contents of the adapter
                aPhotos.clear();
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data"); // Get the data
                    // iterate
                    for (int i=0; i < photosJSON.length(); i++) {
                        // Get the json object
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        // Populate data model
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.id = photoJSON.getString("id");
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        photo.profilePicURL = photoJSON.getJSONObject("user").getString("profile_picture");

                        // Caption can be null; Handle it here
                        if (photoJSON.optJSONObject("caption") != null) {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        } else {
                            photo.caption = "";
                        }

                        photo.type = photoJSON.getString("type");
                        if (photo.type.equals("video")) {
                            // Save the video url
                            photo.videoURL = photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
                        }
                        // All videos have images but vice-versa is not true
                        photo.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.mediaURL = photoJSON.getString("link");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.createdTime = photoJSON.getString("created_time");

                        // Get Location if the media element is geo-tagged
                        if (photoJSON.optJSONObject("location") != null) {
                            Double latitude = photoJSON.getJSONObject("location").getDouble("latitude");
                            Double longitude = photoJSON.getJSONObject("location").getDouble("longitude");
                            photo.location = getAddress(latitude, longitude);
                            Log.d ("MEDIA", photo.location);
                        }

                        // COMMENTS
                        photo.commentsCount = photoJSON.getJSONObject("comments").getInt("count");
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

    /**
     * Reference: http://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddress(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";
        String city = "";
        String state = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return constructAddress(address, city, state);
    }

    // Removes null values and constructs a proper address string
    private String constructAddress(String address, String city, String state) {
        String result = "";
        if (address != null) {
            result += address + ", ";
        }
        if (city != null) {
            result += city + ", ";
        }
        if (state != null) {
            result += state;
        }
        return result;
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

    /**
     * Launches Comments activity when the user clicks on "View all comments" textView
     * @param view
     */
    public void launchCommentsActivity(View view) {
        String mediaId = view.getTag().toString();
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(PhotosActivity.this, CommentsActivity.class);
        i.putExtra("mediaId", mediaId);
        // brings up the Comments activity
        startActivityForResult(i, REQUEST_CODE);
    }

    /**
     * Called when user clicks on the photo
     * It will launch video activity if "type" of media is video
     * @param view
     */
    public void playIfVideo(View view) {
        Object videoURL = view.getTag();
        if (videoURL == null) {
            // Do nothing
            return;
        }
        String url = videoURL.toString();
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(PhotosActivity.this, VideoActivity.class);
        i.putExtra("videoURL", url);
        // brings up the Video activity
        startActivityForResult(i, REQUEST_CODE);
    }
}
