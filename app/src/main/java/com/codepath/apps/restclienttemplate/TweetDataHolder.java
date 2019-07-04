package com.codepath.apps.restclienttemplate;

import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TweetDataHolder {
    private SortedMap<Long, Tweet> mTweets;
    private List<Long> mIds;

    public TweetDataHolder() {
        mTweets = new TreeMap<>();
        mIds = new ArrayList<>();
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

    public int addTweet(Tweet tweet) {



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

    public int size() {
        return mIds.size();
    }

    public long getOldestId() {
        if(mIds.isEmpty())
            return Long.MAX_VALUE;
        return mIds.get(mIds.size() - 1);
    }
}
