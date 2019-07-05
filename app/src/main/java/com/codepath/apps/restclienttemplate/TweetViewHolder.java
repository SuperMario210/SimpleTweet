package com.codepath.apps.restclienttemplate;

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
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.DetailActivity;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
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
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;

    @Nullable @BindView(R.id.tvTimestamp) TextView tvTimestamp;
    @Nullable @BindView(R.id.tvTime) TextView tvTime;
    @Nullable @BindView(R.id.tvDate) TextView tvDate;

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

    public TweetViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindTweet(Tweet tweet, Context context, TwitterClient client, boolean isItem) {
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText(String.format(Locale.getDefault(), "@%s", tweet.user.screenName));
        tvBody.setText(tweet.getBody(context));
        tvBody.setMovementMethod(LinkMovementMethod.getInstance());
        tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
        tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));

        if(isItem) {
            tvTimestamp.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        } else {
            tvTime.setText(Tweet.getTime(tweet.createdAt));
            tvDate.setText(Tweet.getDate(tweet.createdAt));
        }

        if(tweet.isRetweet) {
            ivRetweeted.setVisibility(View.VISIBLE);
            tvRetweeted.setVisibility(View.VISIBLE);
            tvRetweeted.setText(String.format("%s Retweeted", tweet.retweeter.screenName));
        } else {
            ivRetweeted.setVisibility(View.GONE);
            tvRetweeted.setVisibility(View.GONE);
        }

        if(tweet.isReply) {
            tvInReplyTo.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("Replying to @%s", tweet.replyToUserName));

            int color = ContextCompat.getColor(context, R.color.link);
            builder.setSpan(new ForegroundColorSpan(color), 12, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            tvInReplyTo.setText(builder);
        } else {
            tvInReplyTo.setVisibility(View.GONE);
        }

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

        if(tweet.user.isVerified) {
            ivVerified.setVisibility(View.VISIBLE);
        } else {
            ivVerified.setVisibility(View.GONE);
        }

        if(tweet.isRetweeted) {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
            if(isItem)
                tvRetweet.setTextColor(context.getResources().getColor(R.color.retweet));
        } else {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            if(isItem)
                tvRetweet.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }

        ivFavorite.setOnClickListener((View v) -> {
            if(!tweet.isFavorited) {
                tweet.favorite(client, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d("TwitterClient", response.toString());
                            Tweet.updateTweetFromJson(tweet, response);
                            ivFavorite.setAlpha(0f);
                            ivFavoriteAnim.setVisibility(View.VISIBLE);
                            if(isItem)
                                tvFavorite.setTextColor(context.getResources().getColor(R.color.favorite));
                            tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
                            ivFavoriteAnim.setImageResource(R.drawable.anim_heart);
                            AnimationDrawable favAnim = (AnimationDrawable) ivFavoriteAnim.getDrawable();
                            favAnim.start();
                            tweet.isFavorited = true;
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
                        Log.e("favorite", "could not favorite", throwable);
                        Toast.makeText(context, "Couldn't favorite tweet", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                tweet.unfavorite(client, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d("TwitterClient", response.toString());
                            Tweet.updateTweetFromJson(tweet, response);
                            ivFavoriteAnim.setVisibility(View.INVISIBLE);
                            ivFavorite.setAlpha(1f);
                            ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                            tweet.favorites--;
                            tvFavorite.setText(String.format(Locale.getDefault(), "%d", tweet.favorites));
                            if(isItem)
                                tvFavorite.setTextColor(context.getResources().getColor(R.color.dark_gray));
                            tweet.isFavorited = false;
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
                        Log.e("unfavorite", "could not unfavorite", throwable);
                        Toast.makeText(context, "Couldn't unlike tweet", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        ivRetweet.setOnClickListener((View v) -> {
            if(!tweet.isRetweeted) {
                if(isItem)
                    tvRetweet.setTextColor(context.getResources().getColor(R.color.retweet));
                tweet.retweets++;
                tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
                tweet.retweet(client);
                tweet.isRetweeted = true;
            } else {
                if(isItem)
                    tvRetweet.setTextColor(context.getResources().getColor(R.color.dark_gray));
                tweet.retweets--;
                tvRetweet.setText(String.format(Locale.getDefault(), "%d", tweet.retweets));
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                tweet.unRetweet(client);
                tweet.isRetweeted = false;
            }
        });

        ivShare.setOnClickListener((View v) -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, tweet.getUrl());
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        ivReply.setOnClickListener((View v) -> {
            Intent i = new Intent(context, ComposeActivity.class);
            i.putExtra("replyScreenName", tweet.user.screenName);
            i.putExtra("replyTweetId", tweet.uid);
            context.startActivity(i);
        });

        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .error(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .into(ivProfileImage);

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

        if(isItem)
            bindTransition(tweet, context);
    }

    void bindTransition(Tweet tweet, Context context) {
        View.OnClickListener listener = (View v) -> {
            if (tvBody.getSelectionStart() == -1 && tvBody.getSelectionEnd() == -1) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("tweetUid", tweet.uid);

                List<Pair<View, String>> transitions = new ArrayList<>();
                transitions.add(Pair.create(ivProfileImage, "profile"));
                transitions.add(Pair.create(tvUserName, "userName"));
                transitions.add(Pair.create(tvScreenName, "screenName"));
                transitions.add(Pair.create(tvBody, "body"));
                if(!tweet.media.isEmpty()) transitions.add(Pair.create(ivMedia, "media"));
                if(tweet.isRetweet) {
                    transitions.add(Pair.create(tvRetweeted, "retweetText"));
                    transitions.add(Pair.create(ivRetweeted, "retweetImage"));
                }
                if(tweet.isReply) {
                    transitions.add(Pair.create(tvInReplyTo, "inReplyTo"));
                }
                if(tweet.user.isVerified) {
                    transitions.add(Pair.create(ivVerified, "verified"));
                }

                Pair<View, String> pairs[] = new Pair[transitions.size()];
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, transitions.toArray(pairs));

                context.startActivity(i, options.toBundle());
            }
        };

        tvBody.setOnClickListener(listener);
        root.setOnClickListener(listener);
    }
}
