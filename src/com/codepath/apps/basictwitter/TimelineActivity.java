package com.codepath.apps.basictwitter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.basictwitter.ComposeDialog.OnDataPass;
import com.codepath.apps.basictwitter.TweetDetailDialog.OnActionSelectedListenerDetails;
import com.codepath.apps.basictwitter.fragments.DirectMessagesFragment;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.fragments.DirectMessagesFragment.OnActionSelectedListenerDMessages;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnActionSelectedListener;
import com.codepath.apps.basictwitter.listeners.FragmentTabListener;
import com.codepath.apps.basictwitter.listeners.SupportFragmentTabListener;
import com.codepath.apps.basictwitter.models.DirectMessage;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends ActionBarActivity implements 
														OnDataPass,OnActionSelectedListener,OnActionSelectedListenerDetails,OnActionSelectedListenerDMessages {

	private TweetsListFragment fragmentTweetsList;
	
	private TwitterClient client;

	//private long oldestTweetId;
	private long youngestTweetId;
	
	private int numQueries;
	
	private User myUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		numQueries = 0;
		//oldestTweetId = 1;
		youngestTweetId = 1;
		//populateTimeline(oldestTweetId, youngestTweetId, 0);

		client = TwitterApplication.getRestClient();
		
		setupTabs();
		

		
		//if (isNetworkAvailable() == true) {

			getUserCredentials();
		//}
			
	}
	
	private void getUserCredentials() {		
		client.getCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {						
				myUser = User.fromJSON(json);
			}
			
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
				Toast.makeText(getApplicationContext(), "Error identifying the user.", Toast.LENGTH_SHORT)
				.show();
			}
			
		});
	}

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline_activity_actions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            case R.id.miProfile:
                showProfileView(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void showProfileView(MenuItem mi) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("user", myUser);
		//i.putExtra("client", client);
		startActivity(i);
	}

	private void composeMessage() {
			android.app.FragmentManager fm = getFragmentManager();
			ComposeDialog composeDialog = ComposeDialog.newInstance("compose", myUser);
			composeDialog.show(fm, "fragment_compose");
	}
	
	
	
	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
		    .newTab()
		    .setText("Home")
		    //.setIcon(R.drawable.ic_profile)
		    .setTag("HomeTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this,
                        "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
		    .newTab()
		    .setText("Mentions")
		    //.setIcon(R.drawable.ic_profile)
		    .setTag("MentionsTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this,
                        "mentions", MentionsTimelineFragment.class));
		actionBar.addTab(tab2);
		
		Tab tab3 = actionBar
			    .newTab()
			    .setText("Messages")
			    //.setIcon(R.drawable.ic_profile)
			    .setTag("DirectMessagesFragment")
			    .setTabListener(new SupportFragmentTabListener<DirectMessagesFragment>(R.id.flContainer, this,
	                        "mentions", DirectMessagesFragment.class));
		actionBar.addTab(tab3);
	}
	
	
	  // Define the action to take in the activity when the fragment event fires
	  @Override
	  public void onActionSelected(final Tweet tweet, String action) {
			final android.app.FragmentManager fm = getFragmentManager();
			if (action == "reply") {
				ComposeDialog replyDialog = ComposeDialog.newInstance("reply", tweet, myUser);
				replyDialog.show(fm, "fragment_compose");
			} else if (action == "retweet") {
				ComposeDialog retweetDialog = ComposeDialog.newInstance("retweet", tweet, tweet.getUser());
				retweetDialog.show(fm, "fragment_compose");
			} else if (action == "details") {
				TweetDetailDialog tweetDetailDialog = TweetDetailDialog.newInstance(tweet, myUser);
				tweetDetailDialog.show(fm, "fragment_tweet_detail");
			} else if (action == "favorite") {
				client.postFavorite(tweet.getFavorited(), tweet.getUid(), new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json) {
						
						tweet.toggleFavorited();

				        try {
				        	HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) 
				                getSupportFragmentManager().findFragmentById(R.id.flContainer);
				        	homeTimelineFragment.updateFavorite(tweet.getUid());
				        } catch (Exception e) {
				        	e.printStackTrace();
				        }
				        
				        try {
				        	MentionsTimelineFragment mentionsTimelineFragment = (MentionsTimelineFragment) 
				                getSupportFragmentManager().findFragmentById(R.id.flContainer);
				        	mentionsTimelineFragment.updateFavorite(tweet.getUid());
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
					//fragmentTweetsList.insert(Tweet.fromJSON(json), 0);
					HomeTimelineFragment htFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);
					htFragment.insert(Tweet.fromJSON(json), 0);
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
					//fragmentTweetsList.insert(Tweet.fromJSON(json), 0);
					HomeTimelineFragment htFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);
					htFragment.insert(Tweet.fromJSON(json), 0);
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
				}
			});
		} else  if (callFor == "replyDM") {
			client.postDirectMessage(newTweet, replyTweetUid, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					//fragmentTweetsList.insert(Tweet.fromJSON(json), 0);
					DirectMessagesFragment dmFragment = (DirectMessagesFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);
					dmFragment.insert(DirectMessage.fromJSON(json), 0);
					//Toast.makeText(getApplicationContext(), "Success sending message.", Toast.LENGTH_SHORT)
					//.show();
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
					Toast.makeText(getApplicationContext(), "Failed sending message.", Toast.LENGTH_SHORT)
					.show();
				}
			});
		}
	}

	@Override
	public void onActionSelected(DirectMessage dMessage, String action) {
		final android.app.FragmentManager fm = getFragmentManager();
		if (action == "replyDM") {
			ComposeDialog replyDialog = ComposeDialog.newInstance("replyDM", dMessage.getSender());
			replyDialog.show(fm, "fragment_compose");
		} 
	}
	

}
