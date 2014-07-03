package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class FollowersListFragment extends FollowxxxListFragment {
	private TwitterClient client;

	private User myUser;
	
	private long numQueries;

	
    public static FollowersListFragment newInstance(User currUser) {
    	FollowersListFragment fragmentDemo = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", currUser);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		
	    myUser = (User) getArguments().getSerializable("user");

		
		numQueries = 0;
	}
	
	public void populateFollowersList (){

		if (isNetworkAvailable() == true) {
			//Toast.makeText(getApplicationContext(), "since_id " + since_id + "max_id " + max_id, Toast.LENGTH_SHORT)
			//.show();
			numQueries = numQueries + 1;
			
			if (numQueries%100 == 0) {
				Toast.makeText(getActivity(), "Queries made" + numQueries, Toast.LENGTH_LONG)
				.show();
			}

			long cursor;
			if (getCount() == 0) {
				cursor = -1;
			} else {
				cursor = nextCursor;
			}
						
			client.getFollowers(myUser.getUid(), cursor, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
						JSONArray tmpUsers;
					
						try {
							nextCursor = Long.parseLong(json.getString("next_cursor"));
							tmpUsers = json.getJSONArray("users");
						} catch (JSONException e1) {
							tmpUsers = null;
							e1.printStackTrace();
						}
						
						ArrayList<User> followersList = new ArrayList<User>(tmpUsers.length());

						
						for (int i=0; i<tmpUsers.length(); i++) {
							User user = null;
							try {
								user = User.fromJSON(tmpUsers.getJSONObject(i));
								followersList.add(user);
								Toast.makeText(getActivity(), user.getName(), Toast.LENGTH_LONG).show();

							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
						}
							
						addAll(followersList);

						refreshComplete();

					Toast.makeText(getActivity(), "Success" + nextCursor, Toast.LENGTH_LONG)
							.show();
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
					Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG)
					.show();
					refreshComplete();

				}
				
			});
		} else {
			Toast.makeText(getActivity(), "Check your internet connection!", Toast.LENGTH_LONG)
			.show();
			//populateTimelineFromLocalDb();
		}
	}
	
	
	@Override
	public void customLoadMoreDataFromApi(int page) {
		populateFollowersList();
		finishedLoadingData();
	}
	
	@Override
	public void refreshNewData() {
		finishedLoadingData();
	}

}
