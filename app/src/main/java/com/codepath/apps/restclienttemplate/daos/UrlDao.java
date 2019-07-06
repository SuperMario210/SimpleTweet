package com.codepath.apps.restclienttemplate.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Url;

import java.util.List;

@Dao
public interface UrlDao {
    @Query("SELECT * FROM url where tweetUid=:tweetId")
    public List<Url> getUrlsForTweet(long tweetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertUrl(Url url);

    @Delete
    public void deleteUrl(Url url);
}
