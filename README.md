twitterCafe (Twitter Client Redux) <br>
========================

Week 3 project, Twitter Client (TwitterCafe) with all the required, advanced and bonus items implemented. <br> <br>

###User Stories (Given in assigenment): [Completed]

 - Includes all required user stories from Week 3 Twitter Client
 - User can switch between Timeline and Mention views using tabs.
   - User can view their home timeline tweets.
   - User can view the recent mentions of their username.
   - User can scroll to bottom of either of these lists and new tweets will load ("infinite scroll")
   - Optional: Implement tabs in a gingerbread-compatible approach
 - User can navigate to view their own profile
   - User can see picture, tagline, # of followers, # of following, and tweets on their profile.
 - User can click on the profile image in any tweet to see another user's profile.
   - User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
   - Profile view should include that user's timeline

 - Advanced: Robust error handling, check if internet is available, handle error cases, network failures
 - Advanced: When a network request is sent, user sees an indeterminate progress indicator
 - Advanced: User can "reply" to any tweet on their home timeline
   - The user that wrote the original tweet is automatically "@" replied in compose
 - Advanced: User can click on a tweet to be taken to a "detail view" of that tweet
   - Advanced: User can take favorite (and unfavorite) or reweet actions on a tweet
 - Advanced: Improve the user interface and theme the app to feel twitter branded
 - Bonus: User can view their direct messages (or send new ones)

###User Stories (Given in assigenment): [Incomplete]

 - Optional: User can view following / followers list through the profile
   - [NOTE]: Implemented but currently debugging for issues. Will updated if it works.
 - Advanced: User can search for tweets matching a particular query and see results


###Extra User Stories added:

 - Added banner on user profile just like real twitter web format.
 - If a tweet has an image, user will get a preview on the timeline.
 - Shows verified status if a user has identity verified with Twitter.
 - User can see the retweet count in details view.
 - User always sees the timeline partially hidden below the fragments.


Walkthrough of twitterCafe: <br> <br>
![Video Walkthrough](twittercafe.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).
