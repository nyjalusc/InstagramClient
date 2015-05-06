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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "04fa4220195c4f3fa75611784ff3842a";
    private ArrayList<InstagramPhoto> photos;
    private  InstagramPhotosAdapter aPhotos;

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

    }

    // Trgger API request
    public void fetchPopularPhotos() {
//        IG Popular: https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN
//        04fa4220195c4f3fa75611784ff3842a
//                Response
//        Type:  { “data” => [x] => “type”} (“image” or “video”)
//        URL:  {“data” => [x] => “images” => “standard_resolution” => “url"}
//            Caption: {“data” => [x] => “caption” => “text”}
//            Author:  {“data” => [x] => “user” => “username”}

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        // Crate the client
        AsyncHttpClient client = new AsyncHttpClient();
        // Trigger the GET Request
        client.get(url, null, new JsonHttpResponseHandler() {
            // onSuccess (Worked, 200)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Expecting a JSONobject
                //        Type:  { “data” => [x] => “type”} (“image” or “video”)
                //        URL:  {“data” => [x] => “images” => “standard_resolution” => “url"}
                //            Caption: {“data” => [x] => “caption” => “text”}
                //            Author:  {“data” => [x] => “user” => “username”}
                //
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
                       // Caption: {“data” => [x] => “caption” => “text”}
                       photo.caption = photoJSON.getJSONObject("caption").getString("text");
                       // URL:  {“data” => [x] => “images” => “standard_resolution” => “url"}
                       photo.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                       // Type:  { “data” => [x] => “type”} (“image” or “video”)
                       photo.type = photoJSON.getString("type");
                       photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                       photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                       // Add to the arraylist
                       photos.add(photo);
                   }
                } catch (JSONException e) {
                   e.printStackTrace();
                }

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
