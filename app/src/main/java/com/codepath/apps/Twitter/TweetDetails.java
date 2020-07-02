package com.codepath.apps.Twitter;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.Twitter.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetails extends AppCompatActivity {
    ImageView profile;
    ImageView media;
    TextView tstamp;
    TextView name;
    TextView username;
    TextView tweet;
    TextView likes;
    TextView retweets;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        if (android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status));
        }
        getSupportActionBar().setTitle("Tweet");

        profile = findViewById(R.id.ivprofile);
        media = findViewById(R.id.ivMedia);
        tstamp = findViewById(R.id.tvtimestamp);
        name = findViewById(R.id.tvname);
        username = findViewById(R.id.tvusername);
        tweet = findViewById(R.id.tvtweet);
        likes = findViewById(R.id.tvlikes);
        retweets = findViewById(R.id.tvretweet);

        Tweet t = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tweet.setText(t.body);
        username.setText("@"+t.u.username);
        name.setText(t.u.name);
        tstamp.setText(t.createdAt);
        likes.setText(""+t.favoriteCount);
        retweets.setText(""+t.retweetCount);

        Glide.with(TweetDetails.this).load(t.u.profileUrl).transform(new RoundedCornersTransformation(10, 10)).into(profile);
        if (t.mediaURL!= null){
            Glide.with(TweetDetails.this).load(t.mediaURL).transform(new RoundedCornersTransformation(10, 10)).into(media);
        }
        else{
            media.getLayoutParams().width=0;
            media.getLayoutParams().height=0;
        }

    }
}