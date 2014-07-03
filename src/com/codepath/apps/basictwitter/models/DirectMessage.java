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

@Table(name = "DirectMessage")
public class DirectMessage extends Model {
	
    @Column(name = "body")
	private String body;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
    @Column(name = "createdAt")
	private String createdAt;
    @Column(name = "sender")
	private User sender;
    @Column(name = "recipient")
	private User recipient;
    @Column(name = "mediaUrl")
	private String mediaUrl;


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
	
	public User getSender() {
		return sender;
	}
	
	public User getRecipient() {
		return recipient;
	}
	
	public String getMediaUrl() {
		return mediaUrl;
	}

	
	public static ArrayList<DirectMessage> fromJSONArray(JSONArray jsonArray) {
		ArrayList<DirectMessage> dMessages = new ArrayList<DirectMessage>(jsonArray.length());
		
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject dMessageJson = null;
			try {
				dMessageJson = jsonArray.getJSONObject(i);
			} 
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			DirectMessage message = DirectMessage.fromJSON(dMessageJson);
			if (message!=null) {
				dMessages.add(message);
			}
		}
		
		return dMessages;
	}
	
	public static DirectMessage fromJSON (JSONObject jsonObject) {
		DirectMessage message = new DirectMessage();
		//Extract the values from JSON and populate the model
		try {
			message.body = jsonObject.getString("text");
			message.uid = jsonObject.getLong("id");
			message.createdAt = jsonObject.getString("created_at");
			message.sender = User.fromJSON(jsonObject.getJSONObject("sender"));
			message.recipient = User.fromJSON(jsonObject.getJSONObject("recipient"));
			try {
				message.mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
			} catch (Exception e) {
				e.printStackTrace();
				message.mediaUrl = null;
			}

		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return message;
	}
	
	@Override
	public String toString() {
		return getBody() + " - " + getSender().getScreenName() ;
	}
	
    public static List<DirectMessage> getAll() {//Category category) {
        // This is how you execute a query
        return new Select()
          .from(DirectMessage.class)
          .orderBy("uid DESC")
          .execute();
    }
    
    public static void deleteAll(){
    	new Delete().from(DirectMessage.class).execute();
    }
}
