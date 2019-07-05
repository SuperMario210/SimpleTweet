package com.codepath.apps.restclienttemplate.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Hashtag;

import java.util.List;

@Dao
public interface HashtagDao {
    @Query("SELECT * FROM hashtag where tweetUid=:tweetId")
    public List<Hashtag> getHashtagsForTweet(long tweetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertHashtag(Hashtag hashtag);

    @Delete
    public void deleteHashtag(Hashtag hashtag);
}
