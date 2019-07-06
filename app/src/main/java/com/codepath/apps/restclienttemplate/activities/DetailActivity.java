package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetViewHolder;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    Tweet mTweet;
    TwitterClient mClient = TwitterApp.getRestClient(this);
    @BindView(R.id.root) View root;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the tweet that we are displaying
        long tweetUid = getIntent().getLongExtra("tweetUid", 0);
        mTweet = ((TwitterApp) getApplication()).getTweetDataHolder().getTweetByUid(tweetUid);

        ButterKnife.bind(this);

        // Setup the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Format the view based on the tweet information
        TweetViewHolder viewHolder = new TweetViewHolder(root);
        viewHolder.bindTweet(mTweet, this, mClient, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
