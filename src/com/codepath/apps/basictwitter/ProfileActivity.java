package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.apps.basictwitter.ComposeDialog.OnDataPass;
import com.codepath.apps.basictwitter.TweetDetailDialog.OnActionSelectedListenerDetails;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.fragments.FollowxxxListFragment.OnActionSelectedListenerFollowxxx;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnActionSelectedListener;
import com.codepath.apps.basictwitter.listeners.SupportFragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends ActionBarActivity implements OnDataPass,OnActionSelectedListener,OnActionSelectedListenerDetails,OnActionSelectedListenerFollowxxx {
	private User currUser;
	
	private TweetsListFragment fragmentTweetsList;

	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		//setupTabs();
		
		client = TwitterApplication.getRestClient();

		currUser = (User) getIntent().getSerializableExtra("user");
		
		getActionBar().setTitle(currUser.getName());
		loadProfileInfo();
		setupFragment();
	}

	
	
	private void setupFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		UserTimelineFragment userTimeline = UserTimelineFragment.newInstance(currUser);
		ft.replace(R.id.flContainer, userTimeline);
		ft.commit();		
	}



	private void loadProfileInfo() {
		
//		TwitterApplication.getRestClient().getCredentials(new JsonHttpResponseHandler() {
//
//			@Override
//			public void onSuccess(JSONObject json) {
//				User u = User.fromJSON(json);
//				getActionBar().setTitle("@" + u.getScreenName());
//				populateProfileHeader(u);
//				loadProfileBanner(u);
//			}
//
//			@Override
//			public void onFailure(Throwable arg0, JSONObject json) {
//
//			}
//			
//		});
		
		populateProfileHeader(currUser);
		loadProfileBanner(currUser);
		
	}
	
	private void loadProfileBanner(User user) {
		TwitterApplication.getRestClient().getProfileBannerUrl(user.getUid(), new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject json) {
				try {
					String bannerUrl = json.getJSONObject("sizes").getJSONObject("mobile_retina").getString("url");
					updateProfileBanner(bannerUrl);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}


			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
				
			}
			
		});
	}
	
	
	private void populateProfileHeader(User user) {
		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
		TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
		TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		//ImageView ivProfileBackgroundImage = (ImageView) findViewById(R.id.ivProfileBackgroundImage);
		
		tvUserName.setText(user.getName());
		tvTagline.setText(user.getTagline());
		tvFollowers.setText(user.getFollowersCount() + " Followers");
		tvFollowing.setText("Following " + user.getFriendsCount());
		
		String betterResProfileImageUrl =  user.getProfileImageUrl().replaceAll("_normal", "_bigger");
		ImageLoader.getInstance().displayImage(betterResProfileImageUrl, ivProfileImage);
		//ImageLoader.getInstance().displayImage(user.getProfileBackgroundImageUrl(), ivProfileBackgroundImage);
		
		
		
		//Listerners
		tvFollowers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final android.app.FragmentManager fm = getFragmentManager();
				FragmentxxxDialog followDialog = FragmentxxxDialog.newInstance("followers", currUser);
				followDialog.show(fm, "fragment_followxxx");
			}
		});
	}
	

	private void updateProfileBanner(String bannerUrl) {
		ImageView ivProfileBackgroundImage = (ImageView) findViewById(R.id.ivProfileBackgroundImage);
		ImageLoader.getInstance().displayImage(bannerUrl, ivProfileBackgroundImage);
	}
	
//	private void setupTabs() {
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		actionBar.setDisplayShowTitleEnabled(true);
//
//		Tab tab1 = actionBar
//		    .newTab()
//		    .setText("Home")
//		    //.setIcon(R.drawable.ic_profile)
//		    .setTag("HomeTimelineFragment")
//		    .setTabListener(new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this,
//                        "home", HomeTimelineFragment.class));
//
//		actionBar.addTab(tab1);
//		actionBar.selectTab(tab1);
//
//		Tab tab2 = actionBar
//		    .newTab()
//		    .setText("Placeholder")
//		    //.setIcon(R.drawable.ic_profile)
//		    .setTag("Placeholder")
//		    .setTabListener(new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this,
//                        "Placeholder", MentionsTimelineFragment.class));
//		actionBar.addTab(tab2);
//		
//	}
	
	
	  // Define the action to take in the activity when the fragment event fires
	  @Override
	  public void onActionSelected(final Tweet tweet, String action) {
			final android.app.FragmentManager fm = getFragmentManager();
			if (action == "reply") {
				ComposeDialog replyDialog = ComposeDialog.newInstance("reply", tweet, currUser);
				replyDialog.show(fm, "fragment_compose");
			} else if (action == "retweet") {
				ComposeDialog retweetDialog = ComposeDialog.newInstance("retweet", tweet, tweet.getUser());
				retweetDialog.show(fm, "fragment_compose");
			} else if (action == "details") {
				TweetDetailDialog tweetDetailDialog = TweetDetailDialog.newInstance(tweet, currUser);
				tweetDetailDialog.show(fm, "fragment_tweet_detail");
			} else if (action == "favorite") {
				client.postFavorite(tweet.getFavorited(), tweet.getUid(), new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json) {
						//tmpFav = !tmpFav;
						boolean isFav;
						
						try {
							isFav = json.getBoolean("favorited");
							if (isFav) {
								//btnFavorite.setTextColor(Color.parseColor("yellow"));
								//Toast.makeText(getApplicationContext(), "Favorited:" + isFav, Toast.LENGTH_SHORT)
								//.show();
							} else {
								//btnFavorite.setTextColor(Color.parseColor("black"));
								//Toast.makeText(getApplicationContext(), "Favorited:" + isFav, Toast.LENGTH_SHORT)
								//.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						tweet.toggleFavorited();

				        try {
				        	UserTimelineFragment userTimelineFragment = (UserTimelineFragment) 
				                getSupportFragmentManager().findFragmentById(R.id.flContainer);
				        	userTimelineFragment.updateFavorite(tweet.getUid());
				        } catch (Exception e) {
				        	e.printStackTrace();
				        }
				        
				        try {
				        	TweetDetailDialog tweetDetailDialog = (TweetDetailDialog) fm.findFragmentByTag("fragment_tweet_detail");
				        	tweetDetailDialog.updateFavorite(tweet);
				        } catch (Exception e) {
				        	e.printStackTrace();
				        }
					}
					
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s.toString());
					}
				});
			}
				
			
	      //if (composeDialog != null && composeDialog.isInLayout()) {
	      //    fragment.setText(link);
	      //}
			
	  }
	  
		@Override
		public void onDataPass(String newTweet, long replyTweetUid, String callFor) {
		    //Log.d("LOG","hello " + data);

			if (callFor == "compose" || callFor == "reply") {
				client.postUpdate(newTweet, replyTweetUid, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json) {
						fragmentTweetsList.insert(Tweet.fromJSON(json), 0);
					}
					
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s.toString());
					}
				});
			} else  if (callFor == "retweet") {
				client.postRetweet(replyTweetUid, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json) {
						fragmentTweetsList.insert(Tweet.fromJSON(json), 0);
					}
					
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s.toString());
					}
				});
			}
		}
	


	@Override
	public void onActionSelectedListenerFollowxxx(User user, String action) {
		
	}


}
