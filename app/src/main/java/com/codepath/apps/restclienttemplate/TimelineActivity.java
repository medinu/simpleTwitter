package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    TwitterClient client;

    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        //populateHomeTimeline();

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data");
                populateHomeTimeline();
            }});


        rvTweets = findViewById(R.id.rvTweet);

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "OnLoadMore" + page);
                loadMoreData();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeline();

        // find the recycler view
        // innit the list of tweets and adapter
        // recycler view setup: layout manager and the adapter
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose){
            Intent i = new Intent(this, ComposeActivity.class);
            startActivity(i);
        }


        return true;
    }

    private void loadMoreData(){
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "On success for OnLoadMore");
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "On failiure for OnLoadMore");
            }
        }, tweets.get(tweets.size() -1).id);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "On Success"+ json.toString());
                JSONArray jsonArray = json.jsonArray;

                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeContainer.setRefreshing(false);
                   // tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    //adapter.notifyDataSetChanged();
                    Log.i(TAG, "On Success"+ json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "On Success"+ e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On Failiure" + response);
            }
        });


    }
}