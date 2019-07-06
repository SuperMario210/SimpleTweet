package com.codepath.apps.restclienttemplate.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.Media;

@Dao
public interface MediaDao {
    @Query("SELECT * FROM media where uid=:id")
    public Media getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertMedia(Media media);

    @Delete
    public void deleteMedia(Media media);
}