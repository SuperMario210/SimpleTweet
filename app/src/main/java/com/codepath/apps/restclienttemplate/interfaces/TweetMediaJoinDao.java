package com.codepath.apps.restclienttemplate.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetMediaJoin;

import java.util.List;

@Dao
public interface TweetMediaJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TweetMediaJoin tweetMediaJoin);


    @Query("SELECT * FROM tweet INNER JOIN tweet_media_join ON tweet.uid=tweet_media_join.tweetUid WHERE tweet_media_join.mediaUid=:mediaUid")
    List<Tweet> getTweetForMedia(final long mediaUid);

    @Query("SELECT * FROM media INNER JOIN tweet_media_join ON media.uid=tweet_media_join.mediaUid WHERE tweet_media_join.tweetUid=:tweetUid")
    List<Media> getMediaForTweet(final long tweetUid);
}
