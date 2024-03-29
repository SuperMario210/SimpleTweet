package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "TEkkex4tqDbKHf0vWafhdTNdG";
	public static final String REST_CONSUMER_SECRET = "zcDJIfZPTT5Ivo5NqUuQ735ld3zuQVsugnP9Nsc38jx2NKYRVU";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
		client.setTimeout(1000);
		client.setMaxRetriesAndTimeout(1, 1000);
	}

	/**
	 * API call to get user timeline
	 * @param maxId the maximum id to retrieve
	 * @param handler callback
	 */
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("tweet_mode", "extended");
		params.put("max_id", maxId - 1);
		client.get(apiUrl, params, handler);
	}

	/**
	 * API call to post a tweet
	 * @param status the tweet body
	 * @param replyId the id of the tweet bring replied to, 0 if no reply
	 * @param handler callback
	 */
	public void updateStatus(String status, long replyId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", status);
		if(replyId != 0) {
			params.put("in_reply_to_status_id", replyId);
		}
		client.post(apiUrl, params, handler);
	}

	/**
	 * API call to retweet a tweet
	 * @param id the id of the tweet to retweet
	 * @param handler callback
	 */
	public void retweet(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/retweet/" + id + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, handler);
	}

	/**
	 * API call to un-retweet a tweet
	 * @param id the id of the tweet to un-retweet
	 * @param handler callback
	 */
	public void unRetweet(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/unretweet/" + id + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, handler);
	}

	/**
	 * API call to favorite a tweet
	 * @param id the id of the tweet to favorite
	 * @param handler callback
	 */
    public void favorite(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/favorites/create.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

	/**
	 * API call to un-favorite a tweet
	 * @param id the id of the tweet to un-favorite
	 * @param handler callback
	 */
    public void unFavorite(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/favorites/destroy.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }
}
