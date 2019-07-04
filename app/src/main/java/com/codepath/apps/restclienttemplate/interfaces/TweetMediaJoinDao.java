package com.codepath.apps.restclienttemplate.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Media;

import java.util.List;

@Dao
public interface TweetMediaJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TweetMediaJoinDao tweetMediaJoinDao);

    @Query("SELECT * FROM tweet INNER JOIN tweet_media_join ON user.uid=tweet_media_join.userUid WHERE tweet_media_join.tweetUid=:tweetUid")
    List<Media> getMediaForTweet(final int tweetUid);

    @Query("SELECT * FROM media INNER JOIN tweet_media_join ON media.uid=tweet_media_join.mediaUid WHERE tweet_media_join.mediaUid=:mediaUid")
    List<Media> getTweetForMedia(final int mediaUid);
}
