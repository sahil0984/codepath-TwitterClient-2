package com.codepath.apps.basictwitter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	TextView tvAppTitle;
	ProgressBar pbLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		tvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
		tvAppTitle.setText(Html.fromHtml("<b>twitter</b>cafe"));
		// Create the TypeFace from the TTF asset
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Albertsthal_Typewriter.ttf");
		// Assign the typeface to the view
		tvAppTitle.setTypeface(font);
		
    	pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
		
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		getActionBar().hide();

		return true;
	}
	
	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
    	Intent i = new Intent(this, TimelineActivity.class);
    	startActivity(i);
    	//Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
    	
    	pbLogin.setVisibility(ProgressBar.INVISIBLE);
    }
    
    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    	pbLogin.setVisibility(ProgressBar.INVISIBLE);
    }
    
    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
    	pbLogin.setVisibility(ProgressBar.VISIBLE);
    	
        getClient().connect();
    }

}
