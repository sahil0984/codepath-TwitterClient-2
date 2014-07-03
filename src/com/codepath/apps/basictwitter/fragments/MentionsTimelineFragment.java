package com.codepath.apps.basictwitter.fragments;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MentionsTimelineFragment extends TweetsListFragment {

	private TwitterClient client;

	private User myUser;
	
	private long numQueries;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		
		numQueries = 0;
		
		//getUserCredentials();


		//populateTimelineFromLocalDb();
		
		//populateTimeline(1, 1, 0);

	}
	
	public void populateTimeline (long since_id, long max_id, final int type){
		//long max_id = since_id + numTweets;
		if (isNetworkAvailable() == true) {
			//Toast.makeText(getApplicationContext(), "since_id " + since_id + "max_id " + max_id, Toast.LENGTH_SHORT)
			//.show();
			numQueries = numQueries + 1;
			
			if (numQueries%100 == 0) {
				Toast.makeText(getActivity(), "Queries made" + numQueries, Toast.LENGTH_LONG)
				.show();
			}
			
			if (getCount() == 0) {
				Tweet.deleteAll();
				clear();
				notifyDataSetInvalidated();
			}
			
			client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONArray json) {
					ArrayList<Tweet> tmpTweets = Tweet.fromJSONArray(json);
					if (type == 0) { //Scroll - Add more data at the end
						
						addAll(tmpTweets);
						
					} else if (type == 1) { //Refresh - Add new data at the top
						for (int i=0; i<json.length(); i++) {
							insert(tmpTweets.get(i), i);
						}
						
						if (json.length() != 0) {
							try {
								oldestTweetId = Tweet.fromJSON(json.getJSONObject(json.length()-1)).getUid()-1;
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						refreshComplete();
					}
					for (int i=0; i<json.length(); i++) {
						tmpTweets.get(i).getUser().save();
						tmpTweets.get(i).save();
					}

					//Toast.makeText(getActivity(), "oldestTweetId " + oldestTweetId + ". actual_length " + json.length(), Toast.LENGTH_LONG)
					//		.show();
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
			populateTimelineFromLocalDb();
		}
	}
	
	private void populateTimelineFromLocalDb() {
		//try {
			clear();
			notifyDataSetInvalidated();
  			
			List<Tweet> allSavedTweets = (List<Tweet>) Tweet.getAll();
			addAll(allSavedTweets);
	}
	
	@Override
	public void customLoadMoreDataFromApi(int page) {
		long since_id = 1;
		long max_id;
		if (getCount() == 0) {
			max_id = 1;
		} else {
			max_id = (getItem(getCount()-1).getUid())-1;
		}
		populateTimeline(since_id, max_id, 0);
		finishedLoadingData();
	}
	
	@Override
	public void refreshNewData() {
        // Your code to refresh the list contents
        // Make sure you call listView.onRefreshComplete()
        // once the loading is done. This can be done from here or any
        // place such as when the network request has completed successfully.
		long since_id;
		long max_id;
		try {
			since_id = getItem(0).getUid();
		} catch (Exception e) {
			since_id = 1;
		}
		//Toast.makeText(getApplicationContext(), "uid at 0:" + tweets.get(0).getUid(), Toast.LENGTH_LONG)
		//.show();
		max_id = 1;
		populateTimeline(since_id, max_id, 1);
		
		max_id = oldestTweetId;
		
		//Turn it back on
		while (max_id>since_id) {
			populateTimeline(since_id, max_id, 1);
			max_id = oldestTweetId;
		}
		finishedLoadingData();
	}
	

}
