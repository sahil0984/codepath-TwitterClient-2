package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.basictwitter.fragments.FollowersListFragment;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentxxxDialog extends DialogFragment {

	private FragmentActivity myContext;

	private User myUser;
	
	public FragmentxxxDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static FragmentxxxDialog newInstance(String callFor, User user) {
		FragmentxxxDialog  frag = new FragmentxxxDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFor", callFor);
		args.putSerializable("user", user);
	    frag.setArguments(args);
	    return frag;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //getDialog().setTitle("Edit Filters");
        
		View view = inflater.inflate(R.layout.fragment_followxxx, container);
	    //return inflater.inflate(R.layout.fragment_followxxx, null);
		
	    myUser = (User) getArguments().getSerializable("user");


		setupFragment();

	
	return view;
}
	
	private void setupFragment() {
		FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
		FollowersListFragment followersListFragment = FollowersListFragment.newInstance(myUser);
		ft.replace(R.id.flContainer, followersListFragment);
		ft.commit();		
	}

	@Override
	public void onAttach(Activity activity) {
	    myContext=(FragmentActivity) activity;
	    super.onAttach(activity);
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  	  
	  return dialog;
	}

	
	//Passing data from fragment to activity
	//----------------------------------------
	//To pass data back to main activity, create an interface and implement it in main activity
//	public interface OnDataPass {
//	    public void onDataPass(String newTweetBody, long replyTweetUid, String callFor);
//	}
//	OnDataPass dataPasser;
//	@Override
//	public void onAttach(Activity a) {
//	    super.onAttach(a);
//	    dataPasser = (OnDataPass) a;
//	}


	
}
