package com.codepath.apps.restclienttemplate.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT * FROM tweet where uid=:id")
    public Tweet getById(int id);

    @Query("SELECT * FROM tweet")
    public List<Tweet> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertTweet(Tweet tweet);

    @Delete
    public void deleteTweet(Tweet tweet);
}