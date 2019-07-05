package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity(
    tableName = "url",
    primaryKeys = { "tweetUid", "startIndex", "endIndex" }
)
public class Url {
    public long tweetUid;

    public String url;
    public String displayUrl;
    public String fullUrl;
    public int startIndex;
    public int endIndex;

    public Url() {}

    public static Url fromJson(long tweetUid, JSONObject json) throws JSONException {
        Url url = new Url();
        url.tweetUid = tweetUid;

        // extract the values from JSON
        url.url = json.getString("url");
        url.displayUrl = json.getString("display_url");
        url.fullUrl = json.getString("expanded_url");

        JSONArray indices = json.getJSONArray("indices");
        url.startIndex = indices.getInt(0);
        url.endIndex = indices.getInt(1);

        return url;
    }
}
