package com.codepath.apps.restclienttemplate;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.restclienttemplate.interfaces.UserDao;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetMediaJoin;
import com.codepath.apps.restclienttemplate.models.User;

@Database(entities={Tweet.class, Media.class, User.class, TweetMediaJoin.class}, version=1)
public abstract class SimpleTweetDatabase extends RoomDatabase {
    // Declare your data access objects as abstract
    public abstract UserDao userDao();

    // Database name to be used
    public static final String NAME = "SimpleTweetDatabase";
}
