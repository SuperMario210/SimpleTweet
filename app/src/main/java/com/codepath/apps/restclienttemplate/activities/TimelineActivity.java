package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDataHolder;
import com.codepath.apps.restclienttemplate.util.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    public static final int COMPOSE_TWEET_CODE = 1;

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

        // Setup instance variables
        client = TwitterApp.getRestClient(this);
        ButterKnife.bind(this);
        mTweets = ((TwitterApp) getApplication()).getTweetDataHolder();

        // Setup the recycler view adapter
        tweetAdapter = new TweetAdapter(mTweets, this);
        tweetAdapter.setClient(client);
        rvTweet.setAdapter(tweetAdapter);
        
        // Setup the recycler view layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweet.setLayoutManager(linearLayoutManager);
        
        // Setup the scroll listener for infinite pagination
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Load more tweets to the timeline
                populateTimeline();
            }
        };
        rvTweet.addOnScrollListener(scrollListener);

        // Setup the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_vector_twitter_bird); // BIRB!!!

        // Setup refresh listener to refresh timeline when pulled down
        swipeContainer.setOnRefreshListener(() -> {
            tweetAdapter.clear();
            populateTimeline();
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_blue, R.color.medium_green);

        // Load tweets into the timeline
        populateTimeline();
    }

    public void launchCompose(View view) {
        // Start the compose tweet activity
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_TWEET_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == COMPOSE_TWEET_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Add the new tweet to the timeline
                int position = getIntent().getIntExtra("position", 0);
                tweetAdapter.notifyItemChanged(position);
                rvTweet.scrollToPosition(position);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tweetAdapter.notifyDataSetChanged(); //TODO: only update the tweet we need
    }

    private void populateTimeline() {
        client.getHomeTimeline(mTweets.getOldestId(), new JsonHttpResponseHandler() {
            /**
             * Build timeline from response
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                try {
                    // Add all tweets to the timeline then notify the adapter
                    for (int i = 0; i < response.length(); i++) {
                        Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                        tweetAdapter.notifyItemChanged(mTweets.addTweet(tweet, true));
                    }
                } catch (JSONException e) {
                    Log.e("TwitterClient", "Couldn't parse timeline JSON", e);
                }

                // Notify the swipe container
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                loadFailure(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                loadFailure(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadFailure(throwable);
            }

            /**
             * Notify the user then load the timeline from local storage
             * @param throwable
             */
            private void loadFailure(Throwable throwable) {
                // Notify the user
                Log.d("TimelineActivity", "Couldn't load timeline", throwable);
                Toast.makeText(
                        TimelineActivity.this,
                        "Could not connect to server",
                        Toast.LENGTH_LONG).show();

                // Load tweets from local storage
                mTweets.loadFromDatabase(tweetAdapter, TimelineActivity.this);

                // Notify the swipe container
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
