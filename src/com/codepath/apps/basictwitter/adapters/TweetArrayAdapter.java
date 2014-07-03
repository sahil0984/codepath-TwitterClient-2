package com.codepath.apps.basictwitter.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnActionSelectedListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.codepath.apps.basictwitter.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	
    private Context context;
    private OnActionSelectedListener listener;
	
	public TweetArrayAdapter(Context context, List<Tweet> objects, OnActionSelectedListener listener) {
		super(context, 0, objects);
        this.context = context;
        this.listener = listener;
	}

	private Bitmap[] bitmapList;
	private Bitmap bitmapPlaceholder;  

	private void initBitmapListWithPlaceholders(){ 
	// call this whenever the list size changes
	// you can also use a list or a map or whatever so you 
	// don't need to drop all the previously loaded bitmap whenever 
	// the list contents are modified
	    int count = getCount();
	    bitmapList = new Bitmap[count];
	    for(int i=0;i<count;i++){
	         bitmapList[i]=bitmapPlaceholder;
	    }
	}	
	
	private void onBitmapLoaded(int position, Bitmap bmp){
	// this is your callback when the load async is done
	    bitmapList[position] = bmp;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	       // Get the data item for this position
	       final Tweet tweet = getItem(position);    
	       ViewHolder holder;

	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	    	   
	  		LayoutInflater inflater = LayoutInflater.from(getContext());
	  		convertView = inflater.inflate(R.layout.tweet_item, parent, false);
	  	    holder = new ViewHolder();
	  	    
		    // Lookup view for data population
	  	    holder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
	  	    holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
	  	    holder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
	  	    holder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
	  	    holder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
	  	    holder.ivVerifiedStatus = (ImageView) convertView.findViewById(R.id.ivVerifiedStatus);
	  	    holder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
	  	    holder.ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);
	  	    holder.ivFavorite = (ImageView) convertView.findViewById(R.id.ivFavorite);
	  	    holder.ivEmbedImagePreview = (ImageView) convertView.findViewById(R.id.ivEmbedImagePreview);
	  
	  		convertView.setTag(holder);
	  		
//	  		convertView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					listener.onActionSelected(tweet, "details");
//				}
//			});	  		
	  		
	  		
	  		holder.ivProfileImage.setTag(tweet.getUser());
	  		holder.ivProfileImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					User myUser = (User) v.getTag();
					Intent i = new Intent(context, ProfileActivity.class);
					i.putExtra("user", myUser);
					context.startActivity(i);
				}
			});
	  		
	  		holder.ivReply.setTag(tweet);
	  		holder.ivReply.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Tweet tweet = (Tweet) v.getTag();
					listener.onActionSelected(tweet, "reply");
				}
			});
	  		
	  		holder.ivRetweet.setTag(tweet);
	  		holder.ivRetweet.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Tweet tweet = (Tweet) v.getTag();
					listener.onActionSelected(tweet, "retweet");
				}
			});

	  		holder.ivFavorite.setTag(tweet);
	  		holder.ivFavorite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Tweet tweet = (Tweet) v.getTag();
					listener.onActionSelected(tweet, "favorite");
				}
			});
	  		
	  		holder.tvBody.setTag(tweet);
	  		holder.tvBody.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Tweet tweet = (Tweet) v.getTag();
					listener.onActionSelected(tweet, "details");
				}
			});
	  		
	       } else {
	    	   
	    	   holder = (ViewHolder) convertView.getTag();
	    	   holder.ivProfileImage.setTag(tweet.getUser());
	    	   holder.ivReply.setTag(tweet);
	    	   holder.ivRetweet.setTag(tweet);
	    	   holder.ivFavorite.setTag(tweet);
	    	   holder.tvBody.setTag(tweet);

		   	   //To remove the flickering use the holder and the following clear command
		   	   //holder.ivProfileImage.setImageResource(null);
	   	   }
	       
	        //holder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());

	       // Populate the data into the template view using the data object
	       holder.ivProfileImage.setImageResource(android.R.color.transparent);
	       holder.ivEmbedImagePreview.setImageResource(android.R.color.transparent);
	       ImageLoader imageLoader = ImageLoader.getInstance();
	       
	       
	       imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), holder.ivProfileImage);
	       holder.tvUserName.setText(tweet.getUser().getName());
	       holder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
	       holder.tvBody.setText(Html.fromHtml(tweet.getBody()).toString());
	       //holder.tvBody.setText(Long.toString(tweet.getUid()));
	       holder.tvTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
	       holder.ivVerifiedStatus.setImageResource(android.R.color.transparent);
	       if (tweet.getUser().getVerifiedStatus() == true) {
	    	   holder.ivVerifiedStatus.setImageResource(R.drawable.verified_status);
	       } else {
	    	/*   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	    	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	   params.addRule(RelativeLayout.RIGHT_OF, holder.tvUserName.getId());
	    	   holder.tvScreenName.setLayoutParams(params); */
	       }
	       
	       
	       if (tweet.getFavorited()) {
	    	   holder.ivFavorite.setImageResource(R.drawable.ic_favorite_1);
	       } else {
	    	   holder.ivFavorite.setImageResource(R.drawable.ic_favorite_0);
	       }
	       
	       if (tweet.getMediaUrl() != null) {
		     imageLoader.displayImage(tweet.getMediaUrl(), holder.ivEmbedImagePreview);
	       }

	       
	       // Return the completed view to render on screen
	       return convertView;
	}


	static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvBody;
		TextView tvTimestamp;
		ImageView ivVerifiedStatus;
		ImageView ivReply;
		ImageView ivRetweet;
		ImageView ivFavorite;
		ImageView ivEmbedImagePreview;
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
