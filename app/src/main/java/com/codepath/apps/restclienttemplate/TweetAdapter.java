package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    private Context mContext;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUserName.setText(tweet.user.name);
        holder.tvScreenName.setText(String.format(Locale.getDefault(), "@%s", tweet.user.screenName));
        holder.tvBody.setText(tweet.body);
        holder.tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
        holder.tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
        holder.tvTimestamp.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));

        if(tweet.isFavorited) {
            holder.ivFavorite.setImageResource(R.drawable.tile028);
            holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.favorite));
        } else {
            holder.ivFavorite.setVisibility(View.INVISIBLE);
            holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        }

        if(tweet.isRetweeted) {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke_highlight);
            holder.tvRetweet.setTextColor(mContext.getResources().getColor(R.color.retweet));
        } else {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            holder.tvRetweet.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        }

        holder.ivFavorite.setOnClickListener((View v) -> {
            holder.ivFavorite.setVisibility(View.INVISIBLE);
            holder.ivFavoriteAnim.setVisibility(View.VISIBLE);
            holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.favorite));
            holder.tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites + 1));
            AnimationDrawable favAnim = (AnimationDrawable) holder.ivFavoriteAnim.getDrawable();
            favAnim.start();
        });

        holder.ivRetweet.setOnClickListener((View v) -> {
            holder.tvRetweet.setTextColor(mContext.getResources().getColor(R.color.retweet));
            holder.tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets + 1));
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke_highlight);
        });

        GlideApp.with(mContext)
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .error(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .into(holder.ivProfileImage);

        if(!tweet.media.isEmpty()) {
            holder.ivMedia.setVisibility(View.VISIBLE);
            Media media = tweet.media.get(0);

            GlideApp.with(mContext)
                    .load(media.mediaUrl)
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(75)))
                    .into(holder.ivMedia);
        }
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
        @BindView(R.id.root) View root;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
