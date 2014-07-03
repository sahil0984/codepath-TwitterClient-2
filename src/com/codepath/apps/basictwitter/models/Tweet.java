package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import android.util.Log;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	private static final long serialVersionUID = 8034899125965005127L;
	
    @Column(name = "body")
	private String body;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
    @Column(name = "createdAt")
	private String createdAt;
    @Column(name = "user")
	private User user;
    @Column(name = "mediaUrl")
	private String mediaUrl;
    @Column(name = "retweetCount")
	private int retweetCount;
    @Column(name = "favorited")
	private boolean favorited;
    
    @Column(name = "userMentions")
    private ArrayList<String> userMentions;

	//GETTERS
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getMediaUrl() {
		return mediaUrl;
	}
	
	public int getRetweetsCount() {
		return retweetCount;
	}	
	
	public boolean getFavorited() {
		return favorited;
	}
	
	public ArrayList<String> getUserMentions() {
		return userMentions;
	}	
	
	
	
	
	public void toggleFavorited() {
		favorited = !getFavorited();
	}
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
		
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} 
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet!=null) {
				tweets.add(tweet);
			}
		}
		
		return tweets;
	}
	
	public static Tweet fromJSON (JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		//Extract the values from JSON and populate the model
		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			try {
				tweet.mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
			} catch (Exception e) {
				e.printStackTrace();
				tweet.mediaUrl = null;
			}
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			tweet.favorited = jsonObject.getBoolean("favorited");
			
			
			JSONArray mentions = jsonObject.getJSONObject("entities").getJSONArray("user_mentions");
			tweet.userMentions = new ArrayList<String>(mentions.length());
			for (int i=0; i<mentions.length(); i++) {
				tweet.userMentions.add(mentions.getJSONObject(i).getString("screen_name"));
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	@Override
	public String toString() {
		return getBody() + " - " + getUser().getScreenName() ;
	}

	
    public static List<Tweet> getAll() {//Category category) {
        // This is how you execute a query
        return new Select()
          .from(Tweet.class)
          .orderBy("uid DESC")
          .execute();
    }
    
    public static void deleteAll(){
    	new Delete().from(Tweet.class).execute();
    }
}
