package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

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
    public long replyToUserName;
    public boolean isRetweet;
    public boolean isReply;

    @Ignore
    public User retweeter;
    public long retweeterUid;

    @Ignore
    public List<Media> media;

    @Ignore
    public User user;
    public long userUid;

    public Tweet() {}

    public static Tweet updateTweetFromJson(Tweet tweet, JSONObject json) throws JSONException {
        tweet.isRetweet = json.has("retweeted_status");
        if (tweet.isRetweet) {
            tweet.retweeter = User.fromJson(json.getJSONObject("user"));
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
        tweet.createdAt = json.getString("created_at");
        tweet.retweets = json.getInt("retweet_count");
        tweet.favorites = json.getInt("favorite_count");
        tweet.isFavorited = json.getBoolean("favorited");
        tweet.isRetweeted = json.getBoolean("retweeted");
        tweet.user = User.fromJson(json.getJSONObject("user"));

        tweet.media = new ArrayList<>();
        JSONArray mediaJson = json.getJSONObject("entities").optJSONArray("media");
        if(mediaJson == null) return tweet;
        for(int i = 0; i < mediaJson.length(); i++) {
            tweet.media.add(Media.fromJson(mediaJson.getJSONObject(i)));
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

            String timeFormat = "dd MMM yyyy";

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

    public void favorite(TwitterClient client) {
        client.favorite(uid, new JsonHttpResponseHandler() {
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

    public void unfavorite(TwitterClient client) {
        client.unFavorite(uid, new JsonHttpResponseHandler() {
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
}
