package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.R;
import com.vdurmont.emoji.EmojiParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(
    tableName = "tweet"
)
public class Tweet {
    @PrimaryKey public long uid;

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
    public long retweetUid;
    public boolean isReply;
    public int startIndex;
    public int endIndex;

    @Ignore public List<Media> media;
    @Ignore public List<Url> urls;
    @Ignore public List<Hashtag> hashtags;
    @Ignore public List<UserMention> mentions;

    public long userUid;
    @Ignore public User user;
    public long retweeterUid;
    @Ignore public User retweeter;

    public Tweet() {}

    /**
     * Creates a new tweet from json
     */
    public static Tweet fromJson(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet();
        // If the tweet is a retweet, extract data from the original tweet's json
        tweet.isRetweet = json.has("retweeted_status");
        if (tweet.isRetweet) {
            tweet.retweeter = User.fromJson(json.getJSONObject("user"));
            tweet.retweeterUid = tweet.retweeter.uid;
            tweet.retweetUid = json.getLong("id");
            json = json.getJSONObject("retweeted_status");
        }

        tweet.uid = json.getLong("id");

        // Get the tweet text
        if (json.has("full_text")) {
            tweet.body = json.optString("full_text");
        } else {
            tweet.body = json.getString("text");
        }

        // Get the display range
        JSONArray textRange = json.optJSONArray("display_text_range");
        if(textRange == null) {
            tweet.startIndex = 0;
            tweet.endIndex = tweet.body.length();
        } else {
            tweet.startIndex = textRange.getInt(0);
            tweet.endIndex = textRange.getInt(1);
        }

        // Get tweet info
        tweet.createdAt = json.getString("created_at");
        tweet.retweets = json.getInt("retweet_count");
        tweet.favorites = json.getInt("favorite_count");
        tweet.isFavorited = json.getBoolean("favorited");
        tweet.isRetweeted = json.getBoolean("retweeted");
        tweet.user = User.fromJson(json.getJSONObject("user"));
        tweet.userUid = tweet.user.uid;

        // get reply info
        tweet.isReply = !json.isNull("in_reply_to_status_id");
        if(tweet.isReply) {
            tweet.replyToStatusId = json.getLong("in_reply_to_status_id");
            tweet.replyToUserId = json.getLong("in_reply_to_user_id");
            tweet.replyToUserName = json.getString("in_reply_to_screen_name");
        }

        // Extract entities
        JSONObject entities = json.getJSONObject("entities");
        tweet.media = new ArrayList<>();
        tweet.urls = new ArrayList<>();
        tweet.hashtags = new ArrayList<>();
        tweet.mentions = new ArrayList<>();

        // extract media
        JSONArray mediaJson = entities.optJSONArray("media");
        if(mediaJson != null) {
            for (int i = 0; i < mediaJson.length(); i++) {
                tweet.media.add(Media.fromJson(mediaJson.getJSONObject(i)));
            }
        }

        // extract urls
        JSONArray urlJson = entities.getJSONArray("urls");
        for (int i = 0; i < urlJson.length(); i++) {
            tweet.urls.add(Url.fromJson(tweet.uid, urlJson.getJSONObject(i)));
        }

        // extract hashtags
        JSONArray hashtagJson = entities.getJSONArray("hashtags");
        for (int i = 0; i < hashtagJson.length(); i++) {
            tweet.hashtags.add(Hashtag.fromJson(tweet.uid, hashtagJson.getJSONObject(i)));
        }

        // extract user mentions
        JSONArray mentionJson = entities.getJSONArray("user_mentions");
        for (int i = 0; i < mentionJson.length(); i++) {
            tweet.mentions.add(UserMention.fromJson(tweet.uid, mentionJson.getJSONObject(i)));
        }

        return tweet;
    }

    /**
     * Updates an existing tweet from json
     * @throws JSONException
     */
    public static void updateFromJson(Tweet tweet, JSONObject json) throws JSONException {
        if(json.has("retweeted_status")) {
            tweet.retweetUid = json.getLong("id");
            json = json.getJSONObject("retweeted_status");
        }
        tweet.retweets = json.getInt("retweet_count");
        tweet.favorites = json.getInt("favorite_count");
        tweet.isFavorited = json.getBoolean("favorited");
        tweet.isRetweeted = json.getBoolean("retweeted");
    }

    /**
     * Gets a link to this tweet for sharing
     */
    public String getUrl() {
        return "https://twitter.com/" + user.screenName + "/status/" + uid;
    }

    /**
     * Format the tweet body with urls, hashtags, and user mentions
     */
    public SpannableStringBuilder getFormattedBody(Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder(body);
        int linkColor = ContextCompat.getColor(context, R.color.link);

        // highlight hashtags
        for(Hashtag hashtag : hashtags) {
            builder.setSpan(new ForegroundColorSpan(linkColor),
                    getIndex(body, hashtag.startIndex),
                    getIndex(body, hashtag.endIndex),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // highlight user mentions
        for(UserMention mention : mentions) {
            builder.setSpan(new ForegroundColorSpan(linkColor),
                    getIndex(body, mention.startIndex),
                    getIndex(body, mention.endIndex),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // highlight urls and make them clickable
        for(Url url : urls) {
            builder.setSpan(new ForegroundColorSpan(linkColor),
                    getIndex(body, url.startIndex),
                    getIndex(body, url.endIndex),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.url));
                    context.startActivity(browserIntent);
                }

                // keep custom formatting
                @Override
                public void updateDrawState(TextPaint ds) {}
            }, getIndex(body, url.startIndex), getIndex(body, url.endIndex), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // Trim the tweet prefix and suffix and replace urls with the display urls
        builder.replace(getIndex(body, endIndex), builder.length(), "");
        for(int i = urls.size() - 1; i >= 0; i--) {
            builder.replace(getIndex(body, urls.get(i).startIndex), getIndex(body, urls.get(i).endIndex), urls.get(i).displayUrl);
        }
        builder.replace(0, startIndex, "");

        return builder;
    }

    /**
     * Helper method to get the correct index when a string contains emojis
     */
    private static int getIndex(String body, int index) {
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
