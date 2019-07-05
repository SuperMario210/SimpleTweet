package com.codepath.apps.restclienttemplate;

import android.os.AsyncTask;

import com.codepath.apps.restclienttemplate.interfaces.HashtagDao;
import com.codepath.apps.restclienttemplate.interfaces.MediaDao;
import com.codepath.apps.restclienttemplate.interfaces.TweetDao;
import com.codepath.apps.restclienttemplate.interfaces.TweetMediaJoinDao;
import com.codepath.apps.restclienttemplate.interfaces.UrlDao;
import com.codepath.apps.restclienttemplate.interfaces.UserDao;
import com.codepath.apps.restclienttemplate.interfaces.UserMentionDao;
import com.codepath.apps.restclienttemplate.models.Hashtag;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetMediaJoin;
import com.codepath.apps.restclienttemplate.models.Url;
import com.codepath.apps.restclienttemplate.models.UserMention;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TweetDataHolder {
    private SortedMap<Long, Tweet> mTweets;
    private List<Long> mIds;
    private final UserDao mUserDao;
    private final TweetDao mTweetDao;
    private final MediaDao mMediaDao;
    private final UrlDao mUrlDao;
    private final HashtagDao mHashtagDao;
    private final UserMentionDao mUserMentionDao;
    private final TweetMediaJoinDao mTweetMediaJoinDao;

    public TweetDataHolder(SimpleTweetDatabase database) {
        mTweets = new TreeMap<>();
        mIds = new ArrayList<>();

        mUserDao = database.userDao();
        mTweetDao = database.tweetDao();
        mMediaDao = database.mediaDao();
        mUrlDao = database.urlDao();
        mHashtagDao = database.hashtagDao();
        mUserMentionDao = database.userMentionDao();
        mTweetMediaJoinDao = database.tweetMediaJoinDao();
    }

    public void clearData() {
        mTweets.clear();
        mIds.clear();
    }

    public Tweet getTweetByUid(long uid) {
        return mTweets.get(uid);
    }

    public Tweet getTweetByIndex(int index) {
        return mTweets.get(mIds.get(index));
    }

    public int addTweet(Tweet tweet, boolean addToDatbase) {
        if(addToDatbase) {
            addTweetToDatabase(tweet);
        }
        mTweets.put(tweet.uid, tweet);
        for(int i = 0; i < mIds.size(); i++) {
            if(tweet.uid > mIds.get(i)) {
                mIds.add(i, tweet.uid);
                return i;
            }
        }
        mIds.add(tweet.uid);
        return mIds.size() - 1;
    }

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

    public int size() {
        return mIds.size();
    }

    public long getOldestId() {
        if(mIds.isEmpty())
            return Long.MAX_VALUE;
        return mIds.get(mIds.size() - 1);
    }

    public void loadFromDatabase() {
        clearData();
        List<Tweet> tweets = mTweetDao.getAll();
        for(Tweet tweet : tweets) {
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
    }
}
