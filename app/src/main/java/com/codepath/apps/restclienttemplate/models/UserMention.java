package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity(
    tableName = "user_mention",
    primaryKeys = { "tweetUid", "startIndex", "endIndex" }
)
public class UserMention {
    public long tweetUid;

    public long userUid;
    public String screenName;
    public String userName;
    public int startIndex;
    public int endIndex;

    public UserMention() {}

    public static UserMention fromJson(long tweetUid, JSONObject json) throws JSONException {
        UserMention mention = new UserMention();
        mention.tweetUid = tweetUid;

        // extract the values from JSON
        mention.userUid = json.getLong("id");
        mention.userName = json.getString("name");
        mention.screenName = json.getString("screen_name");

        JSONArray indices = json.getJSONArray("indices");
        mention.startIndex = indices.getInt(0);
        mention.endIndex = indices.getInt(1);

        return mention;
    }
}
