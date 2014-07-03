package com.codepath.apps.basictwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "29aN8V4IUD53gp46iI5CWH1m2";       // Change this
    public static final String REST_CONSUMER_SECRET = "WKZL4M1PuAOyTPuG1QbTILespFwpcB548pRnb4w9YYVnglNO05"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
    public void getHomeTimeline (long since_id, long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", Long.toString(since_id));
    	if (max_id != 1) {
    		params.put("max_id", Long.toString(max_id));
    	}
        client.get(apiUrl, params, handler);
    }
    
	public void getMentionsTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/mentions_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", Long.toString(since_id));
    	if (max_id != 1) {
    		params.put("max_id", Long.toString(max_id));
    	}
        client.get(apiUrl, params, handler);
	}
    
    public void getUserTimeline (long user_id, long since_id, long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/user_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", Long.toString(since_id));
    	if (max_id != 1) {
    		params.put("max_id", Long.toString(max_id));
    	}
    	params.put("user_id", Long.toString(user_id));
        client.get(apiUrl, params, handler);
    }
    
    public void getDirectMessages (long since_id, long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("direct_messages.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", Long.toString(since_id));
    	if (max_id != 1) {
    		params.put("max_id", Long.toString(max_id));
    	}
        client.get(apiUrl, params, handler);
    }
    
    public void postDirectMessage (String text, long replyMessageUid, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("direct_messages/new.json");
    	RequestParams params = new RequestParams();
    	params.put("user_id", Long.toString(replyMessageUid));
    	params.put("text", text);
        client.post(apiUrl, params, handler);
    }
    
    public void postUpdate (String status, long replyTweetUid, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", status);
    	if (replyTweetUid != 0) {
    		params.put("in_reply_to_status_id", Long.toString(replyTweetUid));
    	}
        client.post(apiUrl, params, handler);
    }
    
    public void getCredentials (AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);    	
    }
    public void getProfileBannerUrl (long uid, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("users/profile_banner.json");
    	RequestParams params = new RequestParams();
    	params.put("user_id", Long.toString(uid));
        client.get(apiUrl, params, handler);
    }
    
    
    public void postRetweet (long retweetUid, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/retweet/" + Long.toString(retweetUid) + ".json");
        client.post(apiUrl, null, handler);
    }
    
    
    public void postFavorite (boolean fav, long favTweetUid, AsyncHttpResponseHandler handler) {
    	String apiUrl;
    	if (fav) {
    		apiUrl = getApiUrl("favorites/destroy.json");
    	} else {
    		apiUrl = getApiUrl("favorites/create.json");
    	}
    	RequestParams params = new RequestParams();
    	params.put("id", Long.toString(favTweetUid));
        client.post(apiUrl, params, handler);
    }
    
    
    
    public void getFollowers (long uid, long cursor, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("followers/list.json");
    	RequestParams params = new RequestParams();
    	params.put("user_id", Long.toString(uid));
    	params.put("cursor", Long.toString(cursor));
        client.get(apiUrl, params, handler);
    }
    
    //public void postUnFavorite (long favTweetUid, AsyncHttpResponseHandler handler) {
    //	String apiUrl = getApiUrl("favorites/destroy.json");
    //	RequestParams params = new RequestParams();
    //	params.put("id", Long.toString(favTweetUid));
    //    client.post(apiUrl, params, handler);    	
    //}
    
    
    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    /*public void getInterestingnessList(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }*/
    
    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}