package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TweetDataHolder;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    public static final int COMPOSE_TWEET_CODE = 1;

//    private ArrayList<Tweet> tweets;
    private TweetDataHolder mTweets;
    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    @BindView(R.id.rvTweet) RecyclerView rvTweet;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        ButterKnife.bind(this);
        mTweets = ((TwitterApp) getApplication()).getTweetDataHolder();
        tweetAdapter = new TweetAdapter(mTweets, this);
        tweetAdapter.setClient(client);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweet.setLayoutManager(linearLayoutManager);
        rvTweet.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweet.addOnScrollListener(scrollListener);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            tweetAdapter.clear();
            populateTimeline();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_blue,
                R.color.medium_red,
                R.color.medium_green);


        populateTimeline();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeTweet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void composeTweet() {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_TWEET_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == COMPOSE_TWEET_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
                mTweets.addTweet(tweet);
                tweetAdapter.notifyItemChanged(0);
                rvTweet.scrollToPosition(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tweetAdapter.notifyDataSetChanged();
    }

    private void populateTimeline() {
        client.getHomeTimeline(mTweets.getOldestId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                        tweetAdapter.notifyItemChanged(mTweets.addTweet(tweet));
                    }
                } catch (JSONException e) {
                    Log.e("TwitterClient", "Couldn't parse timeline JSON");
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
