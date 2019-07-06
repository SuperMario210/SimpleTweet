package com.codepath.apps.restclienttemplate.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.DetailActivity;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.util.DateFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.root) View root;

    // Image views
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivVerified) ImageView ivVerified;
    @BindView(R.id.ivMedia) ImageView ivMedia;
    @BindView(R.id.ivFavorite) ImageView ivFavorite;
    @BindView(R.id.ivFavoriteAnim) ImageView ivFavoriteAnim;
    @BindView(R.id.ivRetweet) ImageView ivRetweet;
    @BindView(R.id.ivShare) ImageView ivShare;
    @BindView(R.id.ivReply) ImageView ivReply;
    @BindView(R.id.ivRetweeted) ImageView ivRetweeted;

    // Text views
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvFavorite) TextView tvFavorite;
    @BindView(R.id.tvRetweet) TextView tvRetweet;
    @BindView(R.id.tvRetweeted) TextView tvRetweeted;
    @BindView(R.id.tvInReplyTo) TextView tvInReplyTo;
    @Nullable @BindView(R.id.tvTimestamp) TextView tvTimestamp;
    @Nullable @BindView(R.id.tvTime) TextView tvTime;
    @Nullable @BindView(R.id.tvDate) TextView tvDate;

    public TweetViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    /**
     * Populates the view with tweet information
     *
     * @param tweet The tweet whose information should be loaded
     * @param isItem is this viewholder an item in a recycler view
     */
    public void bindTweet(Tweet tweet, Context context, TwitterClient client, boolean isItem) {
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText(String.format(Locale.getDefault(), "@%s", tweet.user.screenName));
        tvBody.setText(tweet.getFormattedBody(context));
        tvBody.setMovementMethod(LinkMovementMethod.getInstance());
        tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
        tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));

        if(isItem) {
            tvTimestamp.setText(DateFormatter.formatTimestamp(tweet.createdAt));
        } else {
            tvTime.setText(DateFormatter.formatTime(tweet.createdAt));
            tvDate.setText(DateFormatter.formatDate(tweet.createdAt));
        }

        // Setup the user's verified icon
        if(tweet.user.isVerified) {
            ivVerified.setVisibility(View.VISIBLE);
        } else {
            ivVerified.setVisibility(View.GONE);
        }

        // Format the retweet header on the tweet view
        if(tweet.isRetweet) {
            ivRetweeted.setVisibility(View.VISIBLE);
            tvRetweeted.setVisibility(View.VISIBLE);
            tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.screenName));
        } else {
            ivRetweeted.setVisibility(View.GONE);
            tvRetweeted.setVisibility(View.GONE);
        }

        // Format the reply header on the tweet view
        if(tweet.isReply) {
            tvInReplyTo.setVisibility(View.VISIBLE);

            // Highlight username
            SpannableStringBuilder builder = new SpannableStringBuilder(
                    String.format("Replying to @%s", tweet.replyToUserName));
            builder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.link)),
                    12,
                    builder.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvInReplyTo.setText(builder);
        } else {
            tvInReplyTo.setVisibility(View.GONE);
        }

        // Format favorite icon and text
        if(tweet.isFavorited) {
            ivFavoriteAnim.setVisibility(View.VISIBLE);
            ivFavoriteAnim.setImageResource(R.drawable.tile028);
            ivFavorite.setAlpha(0f);
            if(isItem)
                tvFavorite.setTextColor(context.getResources().getColor(R.color.favorite));
        } else {
            ivFavoriteAnim.setVisibility(View.INVISIBLE);
            ivFavorite.setAlpha(1f);
            ivFavorite.setImageResource(R.drawable.ic_vector_heart);
            if(isItem)
                tvFavorite.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }

        // Format retweet icon and text
        if(tweet.isRetweeted) {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
            if(isItem)
                tvRetweet.setTextColor(context.getResources().getColor(R.color.retweet));
        } else {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            if(isItem)
                tvRetweet.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }

        // Load profile image
        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .error(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .into(ivProfileImage);

        // Load media image
        if(!tweet.media.isEmpty()) {
            ivMedia.setVisibility(View.VISIBLE);
            Media media = tweet.media.get(0);

            GlideApp.with(context)
                    .load(media.mediaUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(100)))
                    .into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }

        // Bind on click events to elements
        bindOnClickEvents(tweet, context, client, isItem);

        if(isItem)
            bindTransition(tweet, context);
    }

    void bindOnClickEvents(Tweet tweet, Context context, TwitterClient client, boolean isItem) {
        // Make the favorite toggle and call twitter api when clicked
        ivFavorite.setOnClickListener((View v) -> {
            if(!tweet.isFavorited) {
                client.favorite(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            // Update tweet object and database
                            Tweet.updateFromJson(tweet, response);
                            ((TwitterApp)context.getApplicationContext()).getTweetDataHolder().addTweet(tweet, true);

                            // Update favorite count
                            if(isItem)
                                tvFavorite.setTextColor(context.getResources().getColor(R.color.favorite));
                            tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));

                            // Play favorite animation
                            ivFavorite.setAlpha(0f);
                            ivFavoriteAnim.setVisibility(View.VISIBLE);
                            ivFavoriteAnim.setImageResource(R.drawable.anim_heart);
                            AnimationDrawable favAnim = (AnimationDrawable) ivFavoriteAnim.getDrawable();
                            favAnim.start();
                        } catch (JSONException e) {
                            Log.e("TwitterClient", "Couldn't parse JSON");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        notifyFailure(throwable);
                    }

                    public void notifyFailure(Throwable throwable) {
                        // Notify the user
                        Log.e("favorite", "could not favorite", throwable);
                        Toast.makeText(context, "Couldn't favorite tweet", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                client.unFavorite(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            // Update tweet object and database
                            Tweet.updateFromJson(tweet, response);
                            ((TwitterApp)context.getApplicationContext()).getTweetDataHolder().addTweet(tweet, true);

                            // Update text
                            tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
                            if(isItem)
                                tvFavorite.setTextColor(context.getResources().getColor(R.color.dark_gray));

                            // Update icon
                            ivFavoriteAnim.setVisibility(View.INVISIBLE);
                            ivFavorite.setAlpha(1f);
                            ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                        } catch (JSONException e) {
                            Log.e("TwitterClient", "Couldn't parse JSON");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        notifyFailure(throwable);
                    }

                    public void notifyFailure(Throwable throwable) {
                        // Notify the user
                        Log.e("unfavorite", "could not unfavorite", throwable);
                        Toast.makeText(context, "Couldn't unlike tweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Make the favorite toggle and call twitter api when clicked
        ivRetweet.setOnClickListener((View v) -> {
            if(!tweet.isRetweeted) {
                client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d("retweet", response.toString());
                            // Update tweet object and database
                            Tweet.updateFromJson(tweet, response);
                            ((TwitterApp)context.getApplicationContext()).getTweetDataHolder().addTweet(tweet, true);

                            // Update text
                            if(isItem)
                                tvRetweet.setTextColor(context.getResources().getColor(R.color.retweet));
                            tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));

                            // Update Icon
                            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
                        } catch (JSONException e) {
                            Log.e("TwitterClient", "Couldn't parse JSON");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        notifyFailure(throwable);
                    }

                    public void notifyFailure(Throwable throwable) {
                        // Notify the user
                        Log.e("retweet", "could not retweet", throwable);
                        Toast.makeText(context, "Couldn't retweet", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                client.unRetweet(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d("unretweet", response.toString());
                            // Update tweet object and database
                            Tweet.updateFromJson(tweet, response);
                            tweet.isRetweeted = false;
                            ((TwitterApp)context.getApplicationContext()).getTweetDataHolder().addTweet(tweet, true);

                            // Update text
                            if(isItem)
                                tvRetweet.setTextColor(context.getResources().getColor(R.color.dark_gray));
                            tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets - 1));
                            // Update Icon
                            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        } catch (JSONException e) {
                            Log.e("TwitterClient", "Couldn't parse JSON");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        notifyFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        notifyFailure(throwable);
                    }

                    public void notifyFailure(Throwable throwable) {
                        // Notify the user
                        Log.e("retweet", "could not unretweet", throwable);
                        Toast.makeText(context, "Couldn't unretweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Setup click listener for share button
        ivShare.setOnClickListener((View v) -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, tweet.getUrl());
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        // Setup click listener for reply button
        ivReply.setOnClickListener((View v) -> {
            Intent i = new Intent(context, ComposeActivity.class);
            i.putExtra("replyScreenName", tweet.user.screenName);
            i.putExtra("replyTweetId", tweet.uid);
            context.startActivity(i);
        });
    }

    // Setup the transition to the detail activity
    void bindTransition(Tweet tweet, Context context) {
        View.OnClickListener listener = (View v) -> {
            // Make the non-linked text clickable
            if (tvBody.getSelectionStart() == -1 && tvBody.getSelectionEnd() == -1) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("tweetUid", tweet.uid);

                List<Pair<View, String>> transitions = new ArrayList<>();
                transitions.add(Pair.create(ivProfileImage, "profile"));
                transitions.add(Pair.create(tvUserName, "userName"));
                transitions.add(Pair.create(tvScreenName, "screenName"));
                transitions.add(Pair.create(tvBody, "body"));

                // Check if items are visible before adding them to the transition
                if(!tweet.media.isEmpty()) transitions.add(Pair.create(ivMedia, "media"));
                if(tweet.isReply) transitions.add(Pair.create(tvInReplyTo, "inReplyTo"));
                if(tweet.user.isVerified) transitions.add(Pair.create(ivVerified, "verified"));
                if(tweet.isRetweet) {
                    transitions.add(Pair.create(tvRetweeted, "retweetText"));
                    transitions.add(Pair.create(ivRetweeted, "retweetImage"));
                }

                Pair<View, String> pairs[] = new Pair[transitions.size()];
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, transitions.toArray(pairs));

                context.startActivity(i, options.toBundle());
            }
        };

        tvBody.setOnClickListener(listener);
        root.setOnClickListener(listener);
    }
}
