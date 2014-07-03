package com.codepath.apps.basictwitter.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model implements Serializable  {
	private static final long serialVersionUID = -4310710562272456418L;
	
	@Column(name = "name")
	private String name;
    @Column(name = "uid")//, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
    @Column(name = "screenName")
	private String screenName;
    @Column(name = "profileImageUrl")
	private String profileImageUrl;
    @Column(name = "verifiedStatus")
	private boolean verifiedStatus;
    
    @Column(name = "profileBackgroundImageUrl")
	private String profileBackgroundImageUrl;
    @Column(name = "FollowersCount")
	private int FollowersCount;
    @Column(name = "FriendsCount")
	private int FriendsCount;
    @Column(name = "tagline")
	private String tagline;
    

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public boolean getVerifiedStatus() {
		return verifiedStatus;
	}
	
	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}
	public int getFollowersCount() {
		return FollowersCount;
	}
	public int getFriendsCount() {
		return FriendsCount;
	}
	public String getTagline() {
		return tagline;
	}
	
	//User.fromJSON(...)
	public static User fromJSON(JSONObject jsonObject) {
		User user = new User();
		//Extract the values from JSON and populate the model
		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			user.verifiedStatus = (jsonObject.getString("verified")=="true")?true:false;
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			user.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
			user.FollowersCount = jsonObject.getInt("followers_count");
			user.FriendsCount = jsonObject.getInt("friends_count");
			user.tagline = jsonObject.getString("description");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return user;
	}
	
}
