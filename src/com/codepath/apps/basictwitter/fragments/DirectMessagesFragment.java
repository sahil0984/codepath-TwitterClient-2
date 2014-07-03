package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.codepath.apps.basictwitter.EndlessScrollListener;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.adapters.DirectMessageArrayAdapter;
import com.codepath.apps.basictwitter.models.DirectMessage;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class DirectMessagesFragment extends Fragment {
	private TwitterClient client;

	private ArrayList<DirectMessage> dMessages;
	private ArrayAdapter<DirectMessage> aDMessages;
	private eu.erikw.PullToRefreshListView lvDMessages;
	private ProgressBar pbLoading;
	
	long oldestDmId;
	
	User myUser;
	
//INTERFACE LOGIC for passing data form fragment to activity	
	// Define the listener of the interface type
	// listener is the activity itself
	private OnActionSelectedListenerDMessages listener;
	
	// Define the events that the fragment will use to communicate
	public interface OnActionSelectedListenerDMessages {
			public void onActionSelected(DirectMessage dMessage, String action);
	}
	
	// Store the listener (activity) that will have events fired once the fragment is attached
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    if (activity instanceof OnActionSelectedListenerDMessages) {
	    	listener = (OnActionSelectedListenerDMessages) activity;
	    } else {
	        throw new ClassCastException(activity.toString()
	            + " must implement MyListFragment.OnItemSelectedListenerDMessages");
	    }
	}
	
	// Now we can fire the event when the user selects something in the fragment
	//public void onSomeClick(View v) {
	//	listener.onActionSelected(aTweets.getItem(0), "");
	//}
//////////////	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();

		//Non-view initialization
		dMessages = new ArrayList<DirectMessage>();
		aDMessages = new DirectMessageArrayAdapter(getActivity(), dMessages, listener);
		
		oldestDmId = 1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_direct_messages_list, container, false);
		//Assign our view references
		lvDMessages = (eu.erikw.PullToRefreshListView) v.findViewById(R.id.lvDMessages);
		lvDMessages.setAdapter(aDMessages);
	
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		lvDMessages.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long arg3) {
				DirectMessage dMessage = dMessages.get(position);
				
				listener.onActionSelected(dMessage, "details");
			}
		});
		
		lvDMessages.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				pbLoading.setVisibility(ProgressBar.VISIBLE);
				customLoadMoreDataFromApi(page);
			}
			
		});
		
		
		lvDMessages.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				pbLoading.setVisibility(ProgressBar.VISIBLE);
				refreshNewData();
			}
		});
		
		
		//Return the layout view
		return v;
	}
	
	//Delegate the adding to the internal adapter
	public void addAll(ArrayList<DirectMessage> dMessages) {
		aDMessages.addAll(dMessages);
	}
	
	public void insert(DirectMessage dMessage, int pos) {
		aDMessages.insert(dMessage, pos);
	}
	
	public void clear() {
		aDMessages.clear();
	}

	public void notifyDataSetInvalidated() {
		aDMessages.notifyDataSetInvalidated();
	}

	public void addAll(List<DirectMessage> allSavedDMessages) {
		aDMessages.addAll(allSavedDMessages);
	}

	public int getCount() {
		return aDMessages.getCount();
	}

	public DirectMessage getItem(int pos) {
		return aDMessages.getItem(pos);
	}
	
	
	public void refreshComplete() {
		lvDMessages.onRefreshComplete();
	}
	
	
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
		
		max_id = oldestDmId;
		
		//Turn it back on
		while (max_id>since_id) {
			populateTimeline(since_id, max_id, 1);
			max_id = oldestDmId;
		}
		finishedLoadingData();
	}
	
    
    public void finishedLoadingData() {
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }
	
	public void populateTimeline (long since_id, long max_id, final int type){
		//long max_id = since_id + numTweets;
		if (isNetworkAvailable() == true) {
			//Toast.makeText(getApplicationContext(), "since_id " + since_id + "max_id " + max_id, Toast.LENGTH_SHORT)
			//.show();
			//numQueries = numQueries + 1;
			//
			//if (numQueries%100 == 0) {
			//	Toast.makeText(getActivity(), "Queries made" + numQueries, Toast.LENGTH_LONG)
			//	.show();
			//}
			
			if (getCount() == 0) {
				DirectMessage.deleteAll();
				clear();
				notifyDataSetInvalidated();
			}
			
			client.getDirectMessages(since_id, max_id, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONArray json) {
					ArrayList<DirectMessage> tmpDMessages = DirectMessage.fromJSONArray(json);
					if (type == 0) { //Scroll - Add more data at the end
						
						addAll(tmpDMessages);
						
					} else if (type == 1) { //Refresh - Add new data at the top
						for (int i=0; i<json.length(); i++) {
							insert(tmpDMessages.get(i), i);
						}
						
						if (json.length() != 0) {
							try {
								oldestDmId = DirectMessage.fromJSON(json.getJSONObject(json.length()-1)).getUid()-1;
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						refreshComplete();
					}
					for (int i=0; i<json.length(); i++) {
						tmpDMessages.get(i).getSender().save();
						tmpDMessages.get(i).save();
					}

					//Toast.makeText(getActivity(), "oldestDMessageId " + oldestDmId + ". actual_length " + json.length(), Toast.LENGTH_LONG)
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
  			
			List<DirectMessage> allSavedDMessages = (List<DirectMessage>) DirectMessage.getAll();
			addAll(allSavedDMessages);
	}
	
	//TODO: Add a dialog alert??
	public Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

}
