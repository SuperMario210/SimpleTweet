package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.vdurmont.emoji.EmojiParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

@Parcel
@Entity(
    tableName = "tweet"
)
public class Tweet {
    @PrimaryKey
    public long uid;

    public String body;
    public String createdAt;
    public int retweets;
    public int favorites;
    public boolean isFavorited;
    public boolean isRetweeted;

    public long replyToUserId;
    public long replyToStatusId;
    public String replyToUserName;
    public boolean isRetweet;
    public boolean isReply;

    public int startIndex;
    public int endIndex;

    @Ignore
    public User retweeter;
    public long retweeterUid;

    @Ignore
    public List<Media> media;
    @Ignore
    public List<Url> urls;
    @Ignore
    public List<Hashtag> hashtags;
    @Ignore
    public List<UserMention> mentions;

    @Ignore
    public User user;
    public long userUid;

    public Tweet() {}

    public static Tweet updateTweetFromJson(Tweet tweet, JSONObject json) throws JSONException {
        tweet.isRetweet = json.has("retweeted_status");
        if (tweet.isRetweet) {
            tweet.retweeter = User.fromJson(json.getJSONObject("user"));
            tweet.retweeterUid = tweet.retweeter.uid;
            tweet.uid = json.getLong("id");
            json = json.getJSONObject("retweeted_status");
        } else {
            tweet.uid = json.getLong("id");
        }

        // extract the values from JSON
        if (json.has("full_text")) {
            tweet.body = json.optString("full_text");
        } else {
            tweet.body = json.getString("text");
        }

        JSONArray textRange = json.optJSONArray("display_text_range");
        if(textRange == null) {
            tweet.startIndex = 0;
            tweet.endIndex = tweet.body.length();
        } else {
            tweet.startIndex = textRange.getInt(0);
            tweet.endIndex = textRange.getInt(1);
        }

        tweet.createdAt = json.getString("created_at");
        tweet.retweets = json.getInt("retweet_count");
        tweet.favorites = json.getInt("favorite_count");
        tweet.isFavorited = json.getBoolean("favorited");
        tweet.isRetweeted = json.getBoolean("retweeted");
        tweet.user = User.fromJson(json.getJSONObject("user"));
        tweet.userUid = tweet.user.uid;

        tweet.isReply = !json.isNull("in_reply_to_status_id");
        if(tweet.isReply) {
            tweet.replyToStatusId = json.getLong("in_reply_to_status_id");
            tweet.replyToUserId = json.getLong("in_reply_to_user_id");
            tweet.replyToUserName = json.getString("in_reply_to_screen_name");
        }

        tweet.media = new ArrayList<>();
        tweet.urls = new ArrayList<>();
        tweet.hashtags = new ArrayList<>();
        tweet.mentions = new ArrayList<>();

        JSONArray mediaJson = json.getJSONObject("entities").optJSONArray("media");
        if(mediaJson != null) {
            for (int i = 0; i < mediaJson.length(); i++) {
                tweet.media.add(Media.fromJson(mediaJson.getJSONObject(i)));
            }
        }

        JSONArray urlJson = json.getJSONObject("entities").optJSONArray("urls");
        if(urlJson != null) {
            for (int i = 0; i < urlJson.length(); i++) {
                tweet.urls.add(Url.fromJson(tweet.uid, urlJson.getJSONObject(i)));
            }
        }

        JSONArray hashtagJson = json.getJSONObject("entities").optJSONArray("hashtags");
        if(hashtagJson != null) {
            for (int i = 0; i < hashtagJson.length(); i++) {
                tweet.hashtags.add(Hashtag.fromJson(tweet.uid, hashtagJson.getJSONObject(i)));
            }
        }

        JSONArray mentionJson = json.getJSONObject("entities").optJSONArray("user_mentions");
        if(mentionJson != null) {
            for (int i = 0; i < mentionJson.length(); i++) {
                tweet.mentions.add(UserMention.fromJson(tweet.uid, mentionJson.getJSONObject(i)));
            }
        }

        return tweet;
    }

    public static Tweet fromJson(JSONObject json) throws JSONException {
        return updateTweetFromJson(new Tweet(), json);
    }

    public static String getTime(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date date = sf.parse(rawJsonDate);

            String timeFormat = "hh:mm aa";

            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            relativeDate = tf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date date = sf.parse(rawJsonDate);

            String timeFormat = "dd MMM yy";

            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            relativeDate = tf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getUrl() {
        return "https://twitter.com/" + user.screenName + "/status/" + uid;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            String[] words = relativeDate.split(" ");
            if(words.length > 1 && words[words.length - 1].equals("ago")) {
                relativeDate = words[0] + words[1].substring(0, 1);
            }
            if(relativeDate.contains(",")) {
                String timeFormat = "dd MMM yy";
                SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
                relativeDate = tf.format(sf.parse(rawJsonDate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void retweet(TwitterClient client) {
        client.retweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("TwitterClient", response.toString());
                    updateTweetFromJson(Tweet.this, response);
                } catch (JSONException e) {
                    Log.e("TwitterClient", "Couldn't parse JSON");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }

    public void unRetweet(TwitterClient client) {
        client.unRetweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("TwitterClient", response.toString());
                    updateTweetFromJson(Tweet.this, response);
                } catch (JSONException e) {
                    Log.e("TwitterClient", "Couldn't parse JSON");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if(errorResponse != null)
                    Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }

    public void favorite(TwitterClient client, JsonHttpResponseHandler handler) {
        client.favorite(uid, handler);
    }

    public void unfavorite(TwitterClient client, JsonHttpResponseHandler handler) {
        client.unFavorite(uid, handler);
    }

    public SpannableStringBuilder getBody(Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder(body);
        int linkColor = ContextCompat.getColor(context, R.color.link);

        for(Hashtag hashtag : hashtags) {
            builder.setSpan(new ForegroundColorSpan(linkColor), getIndex(body, hashtag.startIndex), getIndex(body, hashtag.endIndex), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        for(UserMention mention : mentions) {
            builder.setSpan(new ForegroundColorSpan(linkColor), getIndex(body, mention.startIndex), getIndex(body, mention.endIndex), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        for(Url url : urls) {
            builder.setSpan(new ForegroundColorSpan(linkColor), url.startIndex, url.endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.url));
                    context.startActivity(browserIntent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {}
            }, getIndex(body, url.startIndex), getIndex(body, url.endIndex), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        builder.replace(getIndex(body, endIndex), builder.length(), "");

        for(int i = urls.size() - 1; i >= 0; i--) {
            builder.replace(getIndex(body, urls.get(i).startIndex), getIndex(body, urls.get(i).endIndex), urls.get(i).displayUrl);
        }


        builder.replace(0, startIndex, "");

        return builder;
    }

    public int getIndex(String body, int index) {
        String resultDecimal = EmojiParser.parseToHtmlDecimal(body);
        for(int i = 0; i < resultDecimal.length() - 1; i++) {
            if(index == 0) {
                resultDecimal = resultDecimal.substring(0, i);
                break;
            }

            if(resultDecimal.charAt(i) == '&' && resultDecimal.charAt(i+1) == '#') {
                while(i < resultDecimal.length() && resultDecimal.charAt(i) != ';') i++;
            }
            index--;
        }
        return EmojiParser.parseToUnicode(resultDecimal).length();
    }
}
