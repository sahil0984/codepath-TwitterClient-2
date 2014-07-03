package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.codepath.apps.basictwitter.EndlessScrollListener;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TweetDetailDialog;
import com.codepath.apps.basictwitter.R.id;
import com.codepath.apps.basictwitter.R.layout;
import com.codepath.apps.basictwitter.adapters.FollowxxxArrayAdapter;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;
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

public abstract class FollowxxxListFragment extends Fragment {
	private ArrayList<User> users;
	private ArrayAdapter<User> aUsers;
	private eu.erikw.PullToRefreshListView lvUsers;
	private ProgressBar pbLoading;
	
	long nextCursor;
	
	User myUser;
	
//INTERFACE LOGIC for passing data form fragment to activity	
	// Define the listener of the interface type
	// listener is the activity itself
	private OnActionSelectedListenerFollowxxx listener;
	
	// Define the events that the fragment will use to communicate
	public interface OnActionSelectedListenerFollowxxx {
			public void onActionSelectedListenerFollowxxx(User user, String action);
	}
	
	// Store the listener (activity) that will have events fired once the fragment is attached
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    if (activity instanceof OnActionSelectedListenerFollowxxx) {
	    	listener = (OnActionSelectedListenerFollowxxx) activity;
	    } else {
	        throw new ClassCastException(activity.toString()
	            + " must implement MyListFragment.OnItemSelectedListenerFollowxxx");
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
		//Non-view initialization
		users = new ArrayList<User>();
		aUsers = new FollowxxxArrayAdapter(getActivity(), users, listener);
		
		nextCursor = -1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_followxxx_list, container, false);
		//Assign our view references
		lvUsers = (eu.erikw.PullToRefreshListView) v.findViewById(R.id.lvFollowxxx);
		lvUsers.setAdapter(aUsers);
	
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		lvUsers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long arg3) {
				User user = users.get(position);
				
				//listener.onActionSelected(tweet, "details");
			}
		});
		
		lvUsers.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				pbLoading.setVisibility(ProgressBar.VISIBLE);
				customLoadMoreDataFromApi(page);
			}
			
		});
		
		
		lvUsers.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				//pbLoading.setVisibility(ProgressBar.VISIBLE);
				//refreshNewData();
			}
		});
		
		
		//Return the layout view
		return v;
	}
	
	//Delegate the adding to the internal adapter
	public void addAll(ArrayList<User> users) {
		aUsers.addAll(users);
	}
	
	public void insert(User user, int pos) {
		aUsers.insert(user, pos);
	}
	
	public void clear() {
		aUsers.clear();
	}

	public void notifyDataSetInvalidated() {
		aUsers.notifyDataSetInvalidated();
	}

	public void addAll(List<User> allSavedUsers) {
		aUsers.addAll(allSavedUsers);
	}

	public int getCount() {
		return aUsers.getCount();
	}

	public User getItem(int pos) {
		return aUsers.getItem(pos);
	}
	
	
	public void refreshComplete() {
		lvUsers.onRefreshComplete();
	}
	
	
    abstract public void customLoadMoreDataFromApi(int page);
	
    abstract public void refreshNewData();	
    
    public void finishedLoadingData() {
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }
	
	
	
	
	//TODO: Add a dialog alert??
	public Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

}
