package com.codepath.apps.restclienttemplate.models;

import android.app.Activity;
import android.os.AsyncTask;

import com.codepath.apps.restclienttemplate.SimpleTweetDatabase;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.daos.HashtagDao;
import com.codepath.apps.restclienttemplate.daos.MediaDao;
import com.codepath.apps.restclienttemplate.daos.TweetDao;
import com.codepath.apps.restclienttemplate.daos.TweetMediaJoinDao;
import com.codepath.apps.restclienttemplate.daos.UrlDao;
import com.codepath.apps.restclienttemplate.daos.UserDao;
import com.codepath.apps.restclienttemplate.daos.UserMentionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data type for quickly directly accessing tweets by id and sorting tweets by id.  Accomplishes
 * this through a combination of a hashmap and an arraylist.  The TweetDataHolder is also not
 * associated with the application as a whole so any class with access to a context can access it.
 *
 * The TweetDataHolder also stores and retrieves tweet data from room.
 */
public class TweetDataHolder {
    private Map<Long, Tweet> mTweets;
    private List<Long> mIds;
    private final UserDao mUserDao;
    private final TweetDao mTweetDao;
    private final MediaDao mMediaDao;
    private final UrlDao mUrlDao;
    private final HashtagDao mHashtagDao;
    private final UserMentionDao mUserMentionDao;
    private final TweetMediaJoinDao mTweetMediaJoinDao;

    public TweetDataHolder(SimpleTweetDatabase database) {
        mTweets = new HashMap<>();
        mIds = new ArrayList<>();

        mUserDao = database.userDao();
        mTweetDao = database.tweetDao();
        mMediaDao = database.mediaDao();
        mUrlDao = database.urlDao();
        mHashtagDao = database.hashtagDao();
        mUserMentionDao = database.userMentionDao();
        mTweetMediaJoinDao = database.tweetMediaJoinDao();
    }

    /**
     * Removes all data from the holder, used for refreshing
     */
    public void clearData() {
        mTweets.clear();
        mIds.clear();
    }

    /**
     * Gets a tweet by uid
     * @param uid
     * @return tweet with the specified uid
     */
    public Tweet getTweetByUid(long uid) {
        return mTweets.get(uid);
    }

    public Tweet getTweetByIndex(int index) {
        return mTweets.get(mIds.get(index));
    }

    /**
     * Adds a tweet to the data holder
     * @param tweet the tweet to store
     * @param addToDatbase backup tweet in local storage
     * @return the index of the tweet in descending order
     */
    public int addTweet(Tweet tweet, boolean addToDatbase) {
        if(addToDatbase) {
            addTweetToDatabase(tweet);
        }
        mTweets.put(tweet.uid, tweet);
        for(int i = 0; i < mIds.size(); i++) {
            if(tweet.uid > mIds.get(i)) {
                mIds.add(i, tweet.uid);
                return i;
            } else if(tweet.uid == mIds.get(i)) {
                return i;
            }
        }
        mIds.add(tweet.uid);
        return mIds.size() - 1;
    }

    /**
     * Adds a tweet to room
     */
    private void addTweetToDatabase(Tweet tweet) {
        AsyncTask.execute(() -> {
            mTweetDao.insertTweet(tweet);
            mUserDao.insertUser(tweet.user);
            if(tweet.isRetweet) {
                mUserDao.insertUser(tweet.retweeter);
            }

            for(Media m : tweet.media) {
                mMediaDao.insertMedia(m);
                mTweetMediaJoinDao.insert(new TweetMediaJoin(tweet.uid, m.uid));
            }

            for(Url u : tweet.urls) {
                mUrlDao.insertUrl(u);
            }

            for(Hashtag h : tweet.hashtags) {
                mHashtagDao.insertHashtag(h);
            }

            for(UserMention um : tweet.mentions) {
                mUserMentionDao.insertMention(um);
            }
        });
    }

    /**
     * @return The number of tweets stored in the holder
     */
    public int size() {
        return mIds.size();
    }

    /**
     * @return The id of the oldest tweet currently stored in the holder
     */
    public long getOldestId() {
        if(mIds.isEmpty())
            return Long.MAX_VALUE;
        return mIds.get(mIds.size() - 1);
    }

    /**
     * Loads tweets from room database
     * @param tweetAdapter
     * @param activity
     */
    public void loadFromDatabase(TweetAdapter tweetAdapter, Activity activity) {
        AsyncTask.execute(() -> {
            clearData();
            List<Tweet> tweets = mTweetDao.getAll();
            for (Tweet tweet : tweets) {
                tweet.user = mUserDao.getById(tweet.userUid);
                tweet.retweeter = mUserDao.getById(tweet.retweeterUid);

                tweet.media = mTweetMediaJoinDao.getMediaForTweet(tweet.uid);
                tweet.media = tweet.media == null ? new ArrayList<>() : tweet.media;

                tweet.urls = mUrlDao.getUrlsForTweet(tweet.uid);
                tweet.urls = tweet.urls == null ? new ArrayList<>() : tweet.urls;

                tweet.hashtags = mHashtagDao.getHashtagsForTweet(tweet.uid);
                tweet.hashtags = tweet.hashtags == null ? new ArrayList<>() : tweet.hashtags;

                tweet.mentions = mUserMentionDao.getMentionsForTweet(tweet.uid);
                tweet.mentions = tweet.mentions == null ? new ArrayList<>() : tweet.mentions;

                addTweet(tweet, false);
            }
            activity.runOnUiThread(tweetAdapter::notifyDataSetChanged);
        });
    }
}
