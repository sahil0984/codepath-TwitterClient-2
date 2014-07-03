package com.codepath.apps.basictwitter.utils;

import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public final class Utils {
	
	static User myUser;
	public static User getMyUser() {
		TwitterApplication.getRestClient().getCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {						
				myUser = User.fromJSON(json);
			}
			
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
		return myUser;
	}
}
