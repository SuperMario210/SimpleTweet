package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDataHolder;

public class TweetAdapter extends RecyclerView.Adapter<TweetViewHolder> {
    private TweetDataHolder mTweets;
    private Context mContext;
    private TwitterClient mClient;

    public TweetAdapter(TweetDataHolder tweets, Context context) {
        mTweets = tweets;
        mContext = context;
    }

    public void setClient(TwitterClient client) {
        mClient = client;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clearData();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        TweetViewHolder viewHolder = new TweetViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TweetViewHolder holder, int position) {
        Tweet tweet = mTweets.getTweetByIndex(position);
        holder.bindTweet(tweet, mContext, mClient, true);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
