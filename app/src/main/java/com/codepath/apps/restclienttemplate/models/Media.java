package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity(
    tableName = "media"
)
public class Media {
    @PrimaryKey
    public long uid;

    public String displayUrl;
    public int startIndex;
    public int endIndex;
    public String mediaUrl;

    public Media() {}

    public static Media fromJson(JSONObject json) throws JSONException {
        Media media = new Media();

        // extract the values from JSON
        media.displayUrl = json.getString("display_url");
        media.uid = json.getLong("id");
        media.mediaUrl = json.getString("media_url_https");

        JSONArray indices = json.getJSONArray("indices");
        media.startIndex = indices.getInt(0);
        media.endIndex = indices.getInt(1);

        return media;
    }
}
