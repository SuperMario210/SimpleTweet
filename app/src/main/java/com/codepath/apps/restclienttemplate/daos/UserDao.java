package com.codepath.apps.restclienttemplate.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user where uid=:id")
    public User getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertUser(User user);

    @Delete
    public void deleteUser(User user);
}