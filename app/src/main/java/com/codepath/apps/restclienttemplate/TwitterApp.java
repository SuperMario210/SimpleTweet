package com.codepath.apps.restclienttemplate;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.codepath.apps.restclienttemplate.models.TweetDataHolder;
import com.facebook.stetho.Stetho;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApp.getRestClient(Context context);
 *     // use client to send requests to API
 *
 */
public class TwitterApp extends Application {
    SimpleTweetDatabase mSimpleTweetDatabase;
    TweetDataHolder mTweetDataHolder;

    @Override
    public void onCreate() {
        super.onCreate();

        mSimpleTweetDatabase = Room.databaseBuilder(this, SimpleTweetDatabase.class,
                SimpleTweetDatabase.NAME).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        mSimpleTweetDatabase.clearAllTables();

        mTweetDataHolder = new TweetDataHolder(mSimpleTweetDatabase);

        // use chrome://inspect to inspect your SQL database
        Stetho.initializeWithDefaults(this);
    }

    public static TwitterClient getRestClient(Context context) {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
    }

    public SimpleTweetDatabase getDatabase() {
        return mSimpleTweetDatabase;
    }

    public TweetDataHolder getTweetDataHolder() {
        return mTweetDataHolder;
    }
}