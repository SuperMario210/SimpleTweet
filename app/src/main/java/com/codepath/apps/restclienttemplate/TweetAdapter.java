package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.activities.DetailActivity;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tweet tweet = mTweets.getTweetByIndex(position);

        holder.tvUserName.setText(tweet.user.name);
        holder.tvScreenName.setText(String.format(Locale.getDefault(), "@%s", tweet.user.screenName));
        holder.tvBody.setText(tweet.body);
        holder.tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
        holder.tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
        holder.tvTimestamp.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));

        if(tweet.isRetweet) {
            holder.ivRetweeted.setVisibility(View.VISIBLE);
            holder.tvRetweeted.setVisibility(View.VISIBLE);
            holder.tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.screenName));
        } else {
            holder.ivRetweeted.setVisibility(View.GONE);
            holder.tvRetweeted.setVisibility(View.GONE);
        }

        if(tweet.isFavorited) {
            holder.ivFavoriteAnim.setVisibility(View.VISIBLE);
            holder.ivFavoriteAnim.setImageResource(R.drawable.tile028);
            holder.ivFavorite.setAlpha(0f);
            holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.favorite));
        } else {
            holder.ivFavoriteAnim.setVisibility(View.INVISIBLE);
            holder.ivFavorite.setAlpha(1f);
            holder.ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
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
            Toast.makeText(mContext, "Clicked", Toast.LENGTH_LONG).show();
            if(!tweet.isFavorited) {
                holder.ivFavorite.setAlpha(0f);
                holder.ivFavoriteAnim.setVisibility(View.VISIBLE);
                holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.favorite));
                tweet.favorites++;
                holder.tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
                holder.ivFavoriteAnim.setImageResource(R.drawable.fav_anim);
                AnimationDrawable favAnim = (AnimationDrawable) holder.ivFavoriteAnim.getDrawable();
                favAnim.start();
                tweet.favorite(mClient);
                tweet.isFavorited = true;
            } else {
                holder.ivFavoriteAnim.setVisibility(View.INVISIBLE);
                holder.ivFavorite.setAlpha(1f);
                holder.ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                tweet.favorites--;
                holder.tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
                holder.tvFavorite.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
                tweet.unfavorite(mClient);
                tweet.isFavorited = false;
            }
        });

        holder.ivRetweet.setOnClickListener((View v) -> {
            if(!tweet.isRetweeted) {
                holder.tvRetweet.setTextColor(mContext.getResources().getColor(R.color.retweet));
                tweet.retweets++;
                holder.tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
                holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke_highlight);
                tweet.retweet(mClient);
                tweet.isRetweeted = true;
            } else {
                holder.tvRetweet.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
                tweet.retweets--;
                holder.tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
                holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                tweet.unRetweet(mClient);
                tweet.isRetweeted = false;
            }
        });

        holder.ivShare.setOnClickListener((View v) -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, tweet.getUrl());
            mContext.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        holder.root.setOnClickListener((View v) -> {
            Intent i = new Intent(mContext, DetailActivity.class);
//            i.putExtra("tweet", Parcels.wrap(tweet));
            i.putExtra("tweetUid", tweet.uid);

            Pair<View, String> p1 = Pair.create(holder.ivProfileImage, "profile");
            Pair<View, String> p2 = Pair.create(holder.tvUserName, "userName");
            Pair<View, String> p3 = Pair.create(holder.tvScreenName, "screenName");
            Pair<View, String> p4 = Pair.create(holder.tvBody, "body");
            Pair<View, String> p5 = Pair.create(holder.ivMedia, "media");
            Pair<View, String> p6 = Pair.create(holder.tvRetweeted, "retweetText");
            Pair<View, String> p7 = Pair.create(holder.ivRetweeted, "retweetImage");
            ActivityOptionsCompat options;
            if(!tweet.media.isEmpty()) {
                if(tweet.isRetweet) {
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3, p4, p5, p6, p7);
                } else {
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3, p4, p5);
                }
            } else {
                if(tweet.isRetweet) {
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3, p4, p6, p7);
                } else {
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3, p4);
                }
            }
            mContext.startActivity(i, options.toBundle());
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
        } else {
            holder.ivMedia.setVisibility(View.GONE);
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
        @BindView(R.id.ivShare) ImageView ivShare;
        @BindView(R.id.root) View root;
        @BindView(R.id.tvRetweeted) TextView tvRetweeted;
        @BindView(R.id.ivRetweeted) View ivRetweeted;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
