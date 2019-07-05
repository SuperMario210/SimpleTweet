package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity(
    tableName = "hashtag",
    primaryKeys = { "tweetUid", "startIndex", "endIndex" }
)
public class Hashtag {
    public long tweetUid;

    public String text;
    public int startIndex;
    public int endIndex;

    public Hashtag() {}

    public static Hashtag fromJson(long tweetUid, JSONObject json) throws JSONException {
        Hashtag hashtag = new Hashtag();
        hashtag.tweetUid = tweetUid;

        // extract the values from JSON
        hashtag.text = json.getString("text");

        JSONArray indices = json.getJSONArray("indices");
        hashtag.startIndex = indices.getInt(0);
        hashtag.endIndex = indices.getInt(1);

        return hashtag;
    }
}
