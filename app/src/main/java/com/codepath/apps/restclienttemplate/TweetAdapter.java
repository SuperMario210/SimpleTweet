package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetViewHolder> {
//    private List<Tweet> mTweets;
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvTimestamp) TextView tvTimestamp;
        @BindView(R.id.ivMedia) ImageView ivMedia;
        @BindView(R.id.ivFavorite) ImageView ivFavorite;
        @BindView(R.id.ivFavoriteAnim) ImageView ivFavoriteAnim;
        @BindView(R.id.tvFavorite) TextView tvFavorite;
        @BindView(R.id.ivRetweet) ImageView ivRetweet;
        @BindView(R.id.tvRetweet) TextView tvRetweet;
        @BindView(R.id.ivShare) ImageView ivShare;
        @BindView(R.id.root) View root;
        @BindView(R.id.tvRetweeted) TextView tvRetweeted;
        @BindView(R.id.ivRetweeted) View ivRetweeted;
        @BindView(R.id.tvInReplyTo) TextView tvInReplyTo;
        @BindView(R.id.ivReply) ImageView ivReply;
        @BindView(R.id.ivVerified) ImageView ivVerified;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
