package com.codepath.apps.restclienttemplate.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.UserMention;

import java.util.List;

@Dao
public interface UserMentionDao {
    @Query("SELECT * FROM user_mention where tweetUid=:tweetId")
    public List<UserMention> getMentionsForTweet(long tweetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertMention(UserMention mention);

    @Delete
    public void deleteMention(UserMention mention);
}
