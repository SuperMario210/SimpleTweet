package com.codepath.apps.restclienttemplate;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.restclienttemplate.daos.HashtagDao;
import com.codepath.apps.restclienttemplate.daos.MediaDao;
import com.codepath.apps.restclienttemplate.daos.TweetDao;
import com.codepath.apps.restclienttemplate.daos.TweetMediaJoinDao;
import com.codepath.apps.restclienttemplate.daos.UrlDao;
import com.codepath.apps.restclienttemplate.daos.UserDao;
import com.codepath.apps.restclienttemplate.daos.UserMentionDao;
import com.codepath.apps.restclienttemplate.models.Hashtag;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetMediaJoin;
import com.codepath.apps.restclienttemplate.models.Url;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.models.UserMention;

@Database(entities={Tweet.class, Media.class, User.class, Url.class, Hashtag.class, UserMention.class, TweetMediaJoin.class}, version=7)
public abstract class SimpleTweetDatabase extends RoomDatabase {
    // Declare your data access objects as abstract
    public abstract UserDao userDao();

    public abstract TweetDao tweetDao();

    public abstract MediaDao mediaDao();

    public abstract UrlDao urlDao();

    public abstract HashtagDao hashtagDao();

    public abstract UserMentionDao userMentionDao();

    public abstract TweetMediaJoinDao tweetMediaJoinDao();

    // Database name to be used
    public static final String NAME = "SimpleTweetDatabase";
}
