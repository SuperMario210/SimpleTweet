package com.codepath.apps.restclienttemplate.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    tableName = "tweet_media_join",
    primaryKeys = { "tweetUid", "mediaUid" },
    foreignKeys = {
        @ForeignKey(
            entity = Tweet.class,
            parentColumns = "uid",
            childColumns = "tweetUid"),
        @ForeignKey(
            entity = Media.class,
            parentColumns = "uid",
            childColumns = "mediaUid")
    }
)
public class TweetMediaJoin {
    public final long tweetUid;
    public final long mediaUid;

    public TweetMediaJoin(final long tweetUid, final long mediaUid) {
        this.tweetUid = tweetUid;
        this.mediaUid = mediaUid;
    }
}