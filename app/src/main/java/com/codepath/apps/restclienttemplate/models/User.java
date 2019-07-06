package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity(
    tableName = "user"
)
public class User {
    @PrimaryKey
    public long uid;

    public String name;
    public String screenName;
    public String profileImageUrl;
    public String bannerImageUrl;
    public boolean isVerified;

    public User() {}

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();

        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.isVerified = json.getBoolean("verified");
        user.profileImageUrl = json.getString("profile_image_url_https").replace("_normal", "");
        user.bannerImageUrl = json.optString("profile_image_url_https").replace("_normal", "");

        return user;
    }
}
