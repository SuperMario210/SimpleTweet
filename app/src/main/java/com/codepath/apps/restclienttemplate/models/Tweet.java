package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public List<Media> media;
    public int retweets;
    public int favorites;
    public boolean isFavorited;
    public boolean isRetweeted;

    public Tweet() {}

    public static Tweet fromJson(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = json.optString("full_text");
        if(tweet.body == null) {
            tweet.body = json.getString("text");
        }
        tweet.uid = json.getLong("id");
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
}
