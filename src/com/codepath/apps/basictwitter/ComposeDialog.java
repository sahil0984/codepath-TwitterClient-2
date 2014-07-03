package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
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

public class ComposeDialog extends DialogFragment {

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvScreenName;
    private EditText etTweetBody;
	private TextView tvTweetCharLeft;
	private Button btnSendTweet;
	
	private TwitterClient client;
	private Tweet newTweet;
	private String newTweetBody;
	
	private Tweet replyTweet;
	private String callFor;
	
	private User myUser;
	
	public ComposeDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static ComposeDialog newInstance(String callFor, User user) {
		ComposeDialog  frag = new ComposeDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFor", callFor);
		args.putSerializable("user", user);
	    frag.setArguments(args);
	    return frag;
	}
	
	public static ComposeDialog newInstance(String callFor, Tweet replyTweet, User user) {
		ComposeDialog  frag = new ComposeDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFor", callFor);
		args.putSerializable("tweet", replyTweet);
		args.putSerializable("user", user);
	    frag.setArguments(args);

	    return frag;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //getDialog().setTitle("Edit Filters");
        
		View view = inflater.inflate(R.layout.fragment_compose, container);
	    ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
	    tvUserName = (TextView) view.findViewById(R.id.tvUserName);
	    tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
		etTweetBody = (EditText) view.findViewById(R.id.etTweetBody);
		tvTweetCharLeft = (TextView) view.findViewById(R.id.tvTweetCharLeft);
		btnSendTweet = (Button) view.findViewById(R.id.btnSendTweet); 
				
		
		callFor = (String) getArguments().getSerializable("callFor");
		myUser = (User) getArguments().getSerializable("user");
		  //getUserCredentials();

		
	    ivProfileImage.setImageResource(android.R.color.transparent);
	    ImageLoader imageLoader = ImageLoader.getInstance();
	    
	    imageLoader.displayImage(myUser.getProfileImageUrl(), ivProfileImage);
	    tvUserName.setText(myUser.getName());
	    tvScreenName.setText("@" + myUser.getScreenName());
		
		if (callFor == "compose") {
			btnSendTweet.setText("Tweet");
		} else if (callFor == "reply") {
			replyTweet = (Tweet) getArguments().getSerializable("tweet");
			
			String replyBody;
			replyBody = "@" + replyTweet.getUser().getScreenName() + " ";
			for (int i=0; i<replyTweet.getUserMentions().size(); i++) {
				replyBody = replyBody + "@" + replyTweet.getUserMentions().get(i) + " ";
			}
			
			etTweetBody.setText(replyBody);
			etTweetBody.setSelection(etTweetBody.getText().length());
			etTweetBody.setFocusableInTouchMode(true);
			etTweetBody.requestFocus();
			btnSendTweet.setText("Reply");
		} else if (callFor == "retweet") {
			replyTweet = (Tweet) getArguments().getSerializable("tweet");
			etTweetBody.setText(replyTweet.getBody());
			etTweetBody.setSelection(etTweetBody.getText().length());
			etTweetBody.setFocusableInTouchMode(true);
			etTweetBody.requestFocus();
			btnSendTweet.setText("Retweet");
			etTweetBody.setFocusable(false);
			etTweetBody.setClickable(false);
		} else if (callFor == "replyDM") {
			btnSendTweet.setText("Send");
			etTweetBody.setHint("Compose new Direct Message....");
		}
		
		//tvTweetCharLeft.setText("140");
		int charsLeft = 140 - etTweetBody.getText().length();
		tvTweetCharLeft.setText(Integer.toString(charsLeft));

		
		etTweetBody.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				try {
					int charsLeft = 140 - s.length();
					tvTweetCharLeft.setText(Integer.toString(charsLeft));
				} catch (Exception e) {
					tvTweetCharLeft.setText("140");
				}
			}
		});
		
		
		btnSendTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ImageFilter filter = new ImageFilter(0,0,0,"");
				//filter.setImageType((int) spnImageType.getSelectedItemId()); 
				//filter.setImageSize((int) spnImageSize.getSelectedItemId()); 
				//filter.setImageColorFilter((int) spnImageColorFilter.getSelectedItemId());
				//filter.setImageSite(etImageSite.getText().toString());
				
				newTweetBody = etTweetBody.getText().toString();
				//dataPasser.onDataPass(newTweetBody);
				
				if (callFor == "compose") {
					dataPasser.onDataPass(newTweetBody, 0, "compose");
				} else if (callFor == "reply") {
					dataPasser.onDataPass(newTweetBody, replyTweet.getUid(), "reply");
				} else if (callFor == "retweet") {
					dataPasser.onDataPass(newTweetBody, replyTweet.getUid(), "retweet");
				} else if (callFor == "replyDM") {
					dataPasser.onDataPass(newTweetBody, myUser.getUid(), "replyDM");
				}
			    
			    dismiss();

			}
		});
		
		return view;
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
	public interface OnDataPass {
	    public void onDataPass(String newTweetBody, long replyTweetUid, String callFor);
	}
	OnDataPass dataPasser;
	@Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    dataPasser = (OnDataPass) a;
	}


	
}
