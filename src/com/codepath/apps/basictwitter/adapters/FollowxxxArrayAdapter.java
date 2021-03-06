package com.codepath.apps.basictwitter.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.basictwitter.ProfileActivity;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.R.id;
import com.codepath.apps.basictwitter.R.layout;
import com.codepath.apps.basictwitter.fragments.DirectMessagesFragment.OnActionSelectedListenerDMessages;
import com.codepath.apps.basictwitter.fragments.FollowxxxListFragment.OnActionSelectedListenerFollowxxx;
import com.codepath.apps.basictwitter.models.DirectMessage;
import com.codepath.apps.basictwitter.models.User;
import com.codepath.apps.basictwitter.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FollowxxxArrayAdapter extends ArrayAdapter<User> {
	
    private Context context;
    private OnActionSelectedListenerFollowxxx listener;
	
	public FollowxxxArrayAdapter(Context context, List<User> objects, OnActionSelectedListenerFollowxxx listener) {
		super(context, 0, objects);
        this.context = context;
        this.listener = listener;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	       // Get the data item for this position
	       User user = getItem(position);
	       ViewHolder holder;

	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	    	   
	  		LayoutInflater inflater = LayoutInflater.from(getContext());
	  		convertView = inflater.inflate(R.layout.followxxx_item, parent, false);
	  	    holder = new ViewHolder();
	  	    
		    // Lookup view for data population
	  	    holder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
	  	    holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
	  	    holder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
	  	    holder.ivVerifiedStatus = (ImageView) convertView.findViewById(R.id.ivVerifiedStatus);
	  
	  		convertView.setTag(holder);	
	  		
	  		
	  		holder.ivProfileImage.setTag(user);
	  		holder.ivProfileImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					User myUser = (User) v.getTag();
					Intent i = new Intent(context, ProfileActivity.class);
					i.putExtra("user", myUser);
					context.startActivity(i);
				}
			});
	  		
	  		
	       } else {
	    	   
	    	   holder = (ViewHolder) convertView.getTag();
	    	   holder.ivProfileImage.setTag(user);

		   	   //To remove the flickering use the holder and the following clear command
		   	   //holder.ivProfileImage.setImageResource(null);
	   	   }
	       
	       
	       // Populate the data into the template view using the data object
	       holder.ivProfileImage.setImageResource(android.R.color.transparent);
	       ImageLoader imageLoader = ImageLoader.getInstance();
	       
	       
	       imageLoader.displayImage(user.getProfileImageUrl(), holder.ivProfileImage);
	       holder.tvUserName.setText(user.getName());
	       holder.tvScreenName.setText("@" + user.getScreenName());
	       //holder.tvBody.setText(Long.toString(tweet.getUid()));
	       holder.ivVerifiedStatus.setImageResource(android.R.color.transparent);
	       if (user.getVerifiedStatus() == true) {
	    	   holder.ivVerifiedStatus.setImageResource(R.drawable.verified_status);
	       } else {
	    	/*   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	    	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	   params.addRule(RelativeLayout.RIGHT_OF, holder.tvUserName.getId());
	    	   holder.tvScreenName.setLayoutParams(params); */
	       }
	       
	       // Return the completed view to render on screen
	       return convertView;
	}


	static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvUserName;
		TextView tvScreenName;
		ImageView ivVerifiedStatus;
	}
	
	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String formattedTime =  relativeDate.replaceAll(" hour.* ago", "h");
		formattedTime =  formattedTime.replaceAll(" minute.* ago", "m");
		formattedTime =  formattedTime.replaceAll(" second.* ago", "s");
		formattedTime =  formattedTime.replaceAll(" day.* ago", "d");
		formattedTime =  formattedTime.replaceAll("Yesterday", "1d");
		formattedTime =  formattedTime.replaceAll(" year.* ago", "y");

		return formattedTime;
	}

	
}
